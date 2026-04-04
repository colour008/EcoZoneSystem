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


	// ================== B端：园区管理接口 ==================
	/**
	 * B端-新增或修改公告(草稿)
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveOrUpdateNotice(NoticeDTO dto) {
		Notice notice = new Notice();
		BeanUtils.copyProperties(dto, notice);
		notice.setPublisherId(SecurityUtils.getUserId());

		if (dto.getId() == null) {
			notice.setStatus(0); // 默认为草稿态
			return noticeMapper.insert(notice) > 0;
		} else {
			// 校验：已发布的公告若要修改，建议先撤回，或者直接更新（大厂通常允许直接更新内容）
			return noticeMapper.updateById(notice) > 0;
		}
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
			targetUserIds = noticeMapper.getSelectedUserIds(notice.getId());
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
		// 1. 直接用 NoticeVO 接收，MyBatis 会根据 XML 中的 NoticeVOResultMap 自动封装
		NoticeVO vo = noticeMapper.getDetailById(id);

		// 2. 判空处理（XML 中的 SQL 已经带了 is_deleted = 0 的条件，所以这里通常只需判 null）
		if (vo == null) {
			throw new BusinessException("公告不存在或已被删除");
		}

		// 3. 增加阅读量（操作数据库）
		noticeMapper.incrementViewCount(id);

		// 4. 如果是登录用户查看，标记为已读
		Long currentUserId = SecurityUtils.getUserId();
		if (currentUserId != null) {
			noticeUserMapper.markAsRead(id, currentUserId, LocalDateTime.now());
			// 既然当前正在看，手动设置 vo 状态为已读，省去再次查询
			vo.setIsRead(true);
		}

		// 5. 直接返回 vo，不需要 BeanUtils.copyProperties 了
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
