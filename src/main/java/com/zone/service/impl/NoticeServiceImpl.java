package com.zone.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.NoticeDTO;
import com.zone.domain.dto.NoticePageQueryDTO;
import com.zone.domain.entity.Notice;
import com.zone.domain.vo.NoticeVO;
import com.zone.domain.vo.UserSelectVO;
import com.zone.mapper.NoticeMapper;
import com.zone.mapper.NoticeTargetMapper;
import com.zone.mapper.NoticeUserMapper;
import com.zone.mapper.UserMapper;
import com.zone.service.NoticeService;
import com.zone.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 通知公告服务实现类
 * @Date: 2026/4/3 21:04
 * @Version: 1.0
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeMapper noticeMapper;
	@Autowired
	private NoticeUserMapper noticeUserMapper;
	@Autowired
	private UserMapper userMapper; // 引入用户Mapper用于全量分发
	@Autowired
	private NoticeTargetMapper noticeTargetMapper; // 专门负责 biz_notice_target 表

	// ================== B端：园区管理接口 ==================

	/**
	 * B端-新增或修改公告(草稿)
	 *
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveOrUpdateNotice(NoticeDTO dto) {
		Notice notice = new Notice();
		BeanUtils.copyProperties(dto, notice);
		notice.setPublisherId(SecurityUtils.getUserId());

		boolean success;
		if (dto.getId() == null) {
			notice.setStatus(0);
			success = noticeMapper.insert(notice) > 0;
		} else {
			success = noticeMapper.updateById(notice) > 0;
		}

		// 维护定向推送配置
		if (success) {
			// 无论何种情况，先清理旧的配置（针对修改场景）
			noticeTargetMapper.deleteByNoticeId(notice.getId());

			// 如果是指定用户范围，且传了 ID 集合
			if (dto.getTargetScope() == 1 && dto.getTargetUserIds() != null && !dto.getTargetUserIds().isEmpty()) {
				// 批量插入到 biz_notice_target 表
				noticeTargetMapper.batchInsert(notice.getId(), dto.getTargetUserIds());
			}
		}
		return success;
	}

	/**
	 * 发布逻辑：大厂规范中，发布公告需要将消息推送到用户的“收件箱”
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean publish(Long id) {
		Notice notice = noticeMapper.selectById(id);
		if (notice == null) throw new BusinessException(ResponseCodeEnum.NOTICE_NOT_EXIST);

		// 1. 更新主表状态
		notice.setStatus(1);
		notice.setPublishTime(LocalDateTime.now());
		noticeMapper.updateById(notice);

		// 2. 分发逻辑 (同步至 biz_notice_user 表)，在大厂生产环境下，这里会通过 MQ 异步分发，避免阻塞
		distributeNotice(notice);
		return true;
	}

	/**
	 * 私有分发逻辑：发布公告需要将消息推送到用户的“收件箱”
	 */
	private void distributeNotice(Notice notice) {
		// 先清理旧的分发记录（防止重复点击发布）
		noticeUserMapper.deleteByNoticeId(notice.getId());

		List<Long> targetUserIds;
		if (notice.getTargetScope() == 0) {
			// 全量分发：获取所有在职/正常状态的用户ID
			targetUserIds = userMapper.selectAllActiveUserIds();
		} else {
			// 定向分发：从关联业务表或 DTO 缓存中获取已选定的用户
			targetUserIds = noticeTargetMapper.selectUserIdsByNoticeId(notice.getId());
		}

		if (targetUserIds != null && !targetUserIds.isEmpty()) {
			// 批量插入，提高性能
			noticeUserMapper.batchInsert(notice.getId(), targetUserIds);
		}
	}

	/**
	 * 撤回逻辑：撤回公告需要将消息从用户的“收件箱”中移除
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean recall(Long id) {
		Notice notice = noticeMapper.selectById(id);
		if (notice.getStatus() != 1) {
			throw new BusinessException("只有已发布的公告才能撤回");
		}

		// 1. 修改状态为已撤回
		notice.setStatus(2);
		noticeMapper.updateById(notice);

		// 2. 移除收件箱记录（撤回意味着用户不再能看到这则通知）
		noticeUserMapper.deleteByNoticeId(id);
		return true;
	}

	/**
	 * 存档逻辑：将公告保存至历史表，并删除当前表记录
	 */
	@Override
	public boolean archive(Long id) {
		return noticeMapper.updateStatus(id, 3) > 0;
	}

	/**
	 * B端-全量分页列表
	 */
	@Override
	public PageResult<NoticeVO> getAdminPage(NoticePageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<NoticeVO> page = noticeMapper.getAdminNoticePage(dto);
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	/**
	 * 批量删除公告
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}

		// 1. 查出所有要删除的公告进行状态校验
		List<Notice> notices = noticeMapper.selectByIds(ids);
		if (notices == null || notices.isEmpty()) {
			return false;
		}

		// 校验：不能直接删除处于“已发布(1)”状态的公告
		boolean hasPublished = notices.stream().anyMatch(n -> n.getStatus() != null && n.getStatus() == 1);
		if (hasPublished) {
			throw new BusinessException("已发布的公告不能直接删除，请先撤回！");
		}

		// 2. 主表逻辑删除 (设置 is_deleted = 1)
		int rows = noticeMapper.logicalDeleteByIds(ids);

		// 3. 物理删除关联的 C端收件箱记录
		// 既然公告都被删除了，用户也没必要看到这条数据的未读状态了
		if (rows > 0) {
			// 物理删除收件箱记录 (biz_notice_user)
			noticeUserMapper.deleteByNoticeIds(ids);

			// 物理删除目标配置记录 (biz_notice_target)
			noticeTargetMapper.deleteByNoticeIds(ids);
		}

		return rows > 0;
	}

	// ================== C端：企业用户接口 ==================

	/**
	 * C端-获取政策/动态/公告列表
	 */
	@Override
	public PageResult<NoticeVO> getPublicPage(NoticePageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());

		// C端查询：status=1, visibility=1, 且按发布时间倒序
		// 传入当前 userId 用于 Join 查询 is_read 状态
		Long currentUserId = SecurityUtils.getUserId();
		Page<NoticeVO> page = noticeMapper.getPublicNoticePage(dto, currentUserId);

		return new PageResult<>(page.getTotal(), page.getResult());
	}

	/**
	 * C端-获取公告详情
	 */
	@Override
	public NoticeVO getDetail(Long id) {
		// 1. 获取当前文章详情
		NoticeVO vo = noticeMapper.getDetailById(id);

		if (vo == null) {
			throw new BusinessException("公告不存在或已被删除");
		}

		// 2.获取同类型的上一篇和下一篇，传入当前文章的 type 和 publishTime
		vo.setPrevNotice(noticeMapper.selectPrevNotice(vo.getType(), vo.getPublishTime()));
		vo.setNextNotice(noticeMapper.selectNextNotice(vo.getType(), vo.getPublishTime()));

		// 3. 原有逻辑：处理指定用户范围
		if (vo.getTargetScope() == 1) {
			List<Long> targetUserIds = noticeTargetMapper.selectUserIdsByNoticeId(id);
			vo.setTargetUserIds(targetUserIds);
		}

		// 4. 原有逻辑：增加阅读量
		noticeMapper.incrementViewCount(id);

		// 5. 原有逻辑：标记已读
		Long currentUserId = SecurityUtils.getUserId();
		System.out.println("currentUserId:"+currentUserId);
		if (currentUserId != null) {
			// 尝试更新已读状态
			int rows = noticeUserMapper.markAsRead(id, currentUserId, LocalDateTime.now());

			// 如果更新行数为0，说明该用户在 biz_notice_user 中还没有记录
			if (rows == 0) {
				// 检查是否是因为已经是已读（所以is_read=0的条件不满足），还是因为没记录
				// 这里可以直接尝试插入一条记录（注意处理唯一索引冲突）
				try {
					// 在 NoticeUserMapper 插入已读记录
					noticeUserMapper.insertReadRecord(id, currentUserId, LocalDateTime.now());
				} catch (Exception e) {
					// 可能是并发下别人刚插入成功，忽略即可
				}
			}
			vo.setIsRead(true);
		}

		return vo;
	}

	/**
	 * C端-获取当前用户未读消息数
	 */
	@Override
	public int getUnreadCount() {
		return noticeUserMapper.countUnreadByUserId(SecurityUtils.getUserId());
	}

	/**
	 * 获取所有在职/正常状态的用户ID
	 */
	@Override
	public List<UserSelectVO> getActiveUserSelect() {
		List<UserSelectVO> users = userMapper.selectActiveUserVOs();
		return users;
	}
}
