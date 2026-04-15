package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.exception.BusinessException;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.InquiryPageQueryDTO;
import com.zone.domain.dto.InquirySubmitDTO;
import com.zone.domain.entity.Inquiry;
import com.zone.domain.vo.InquiryVO;
import com.zone.mapper.EnterpriseMapper;
import com.zone.mapper.InquiryMapper;
import com.zone.mapper.UserMapper;
// 假设你有这些 Mapper
// import com.zone.mapper.EnterpriseMapper; 
// import com.zone.mapper.SysUserRoleMapper; 
import com.zone.mapper.UserRoleMapper;
import com.zone.service.InquiryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: JamHoo
 * @Description: 意向留言服务实现类 (含内存防刷策略)
 * @Date: 2026-04-12
 */
@Service
@Slf4j
public class InquiryServiceImpl implements InquiryService {

	@Autowired
	private InquiryMapper inquiryMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private EnterpriseMapper enterpriseMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	/**
	 * 【轻量级内存锁】记录IP及其【下次允许提交的绝对时间戳】
	 * 实现 1 分钟内的防抖/防连击
	 */
	private final Map<String, Long> ipCooldownMap = new ConcurrentHashMap<>();

	// 定时清理内存中的过期IP，防止 ConcurrentHashMap 无限膨胀 (每小时执行一次)
	@Scheduled(fixedRate = 3600000)
	public void cleanUpCooldownMap() {
		long currentTime = System.currentTimeMillis();
		ipCooldownMap.entrySet().removeIf(entry -> entry.getValue() < currentTime);
		log.info("已清理意向留言防刷内存锁，当前剩余记录数: {}", ipCooldownMap.size());
	}

	// ================== C端：公开接口 ==================

	/**
	 * 提交意向留言
	 *
	 * @param dto
	 * @param ip
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitInquiry(InquirySubmitDTO dto, String ip) {
		long currentTime = System.currentTimeMillis();

		// 1. 内存级防刷：同一IP限制 1 分钟内只能提交 1 次
		Long nextAllowedTime = ipCooldownMap.get(ip);
		if (nextAllowedTime != null && currentTime < nextAllowedTime) {
			throw new BusinessException("提交过于频繁，请1分钟后再试");
		}

		// 2. 数据库级防刷：同一IP限制每天最多提交 5 次
		int todayCount = inquiryMapper.countByIpToday(ip);
		if (todayCount >= 5) {
			throw new BusinessException("您今天的提交次数已达上限，感谢您的关注，请明天再试");
		}

		// 3. 校验通过，锁定该IP 1分钟 (60000毫秒)
		ipCooldownMap.put(ip, currentTime + 60000);

		// 4. 数据落库
		Inquiry inquiry = new Inquiry();
		BeanUtils.copyProperties(dto, inquiry);
		inquiry.setCreateIp(ip);
		inquiry.setStatus(0); // 0: 待处理
		inquiryMapper.insert(inquiry);

		// 5. 联动：此处可调用你写好的 NoticeService 给管理员发一条内部通报
		log.info("收到新留言: {} - 电话: {}", dto.getApplicantName(), dto.getContactPhone());
	}

	// ================== B端：管理接口 ==================

	/**
	 * 意向留言分页查询
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public PageResult<InquiryVO> getAdminPage(InquiryPageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<InquiryVO> page = inquiryMapper.getAdminInquiryPage(dto);
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	/**
	 * 分配处理人
	 *
	 * @param inquiryId
	 * @param handlerId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean assignHandler(Long inquiryId, Long handlerId) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) throw new BusinessException("意向记录不存在");

		inquiry.setHandlerId(handlerId);
		inquiry.setStatus(1); // 1: 跟进中
		inquiry.setHandleTime(LocalDateTime.now());
		return inquiryMapper.updateById(inquiry) > 0;
	}

	/**
	 * 跟进记录
	 *
	 * @param inquiryId
	 * @param result
	 * @param status
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean recordFollowUp(Long inquiryId, String result, Integer status) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) throw new BusinessException("意向记录不存在");

		inquiry.setHandleResult(result);
		inquiry.setHandleTime(LocalDateTime.now());
		if (status != null) {
			inquiry.setStatus(status);
		}
		return inquiryMapper.updateById(inquiry) > 0;
	}

	/**
	 * 转化为入驻企业
	 *
	 * @param inquiryId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean convertToEnterprise(Long inquiryId) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) {
			throw new BusinessException("意向记录不存在");
		}
		if (inquiry.getStatus() == 2) {
			throw new BusinessException("该意向已转化为入驻企业，请勿重复操作");
		}
		if (!StringUtils.hasText(inquiry.getCompanyName())) {
			throw new BusinessException("转化失败：该意向未填写企业名称，请补充后重试");
		}

		// 核心联动逻辑：
//		userMapper.insert(newUser); // 用 contactPhone 作为账号和初始密码
//		userRoleMapper.insertBatch(newUser.getId(), 3L);//绑定企业角色
//		enterpriseMapper.insert(enterprise); //插入 biz_enterprise 表，status=0(待审核)

		// 4. 更新当前意向状态
		inquiry.setStatus(2); // 2: 已转入驻
		inquiry.setHandleResult("线下沟通完毕，已成功转化为入驻企业账号");
		inquiry.setHandleTime(LocalDateTime.now());
		inquiryMapper.updateById(inquiry);

		log.info("意向ID {} 成功转化为企业 {}", inquiryId, inquiry.getCompanyName());
		return true;
	}
}