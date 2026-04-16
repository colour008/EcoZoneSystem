package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.exception.BusinessException;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.InquiryPageQueryDTO;
import com.zone.domain.dto.InquirySubmitDTO;
import com.zone.domain.entity.*;
import com.zone.domain.vo.InquiryVO;
import com.zone.mapper.*;
import com.zone.service.InquiryService;
import com.zone.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: JamHoo
 * @Description: 意向留言服务实现类 (高度优化版)
 * @Date: 2026-04-16
 */
@Service
@Slf4j
public class InquiryServiceImpl implements InquiryService {

	// 状态常量定义
	private static final int STATUS_WAIT = 0;      // 待处理
	private static final int STATUS_FOLLOWING = 1; // 跟进中
	private static final int STATUS_CONVERTED = 2; // 已转入驻
	private static final int STATUS_FINISHED = 3;  // 已完结
	private static final int STATUS_INVALID = 4;   // 无效记录

	// 流水动作类型
	private static final int ACTION_ASSIGN = 1;    // 分配
	private static final int ACTION_FOLLOW = 2;    // 跟进
	private static final int ACTION_CONVERT = 3;   // 转化
	private static final int ACTION_CLOSE = 4;     // 关闭/删除

	@Autowired
	private InquiryMapper inquiryMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private EnterpriseMapper enterpriseMapper;
	@Autowired
	private EnterpriseAuditMapper enterpriseAuditMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private InquiryRecordMapper inquiryRecordMapper;

	private final Map<String, Long> ipCooldownMap = new ConcurrentHashMap<>();

	@Scheduled(fixedRate = 3600000)
	public void cleanUpCooldownMap() {
		long currentTime = System.currentTimeMillis();
		ipCooldownMap.entrySet().removeIf(entry -> entry.getValue() < currentTime);
	}

	/**
	 * 校验是否为终态
	 */
	private void checkTerminalState(Inquiry inquiry) {
		if (inquiry.getStatus() == STATUS_CONVERTED ||
				inquiry.getStatus() == STATUS_FINISHED ||
				inquiry.getStatus() == STATUS_INVALID) {
			throw new BusinessException("该意向已处于终态，禁止继续操作");
		}
	}

	// ================== C端：公开接口 ==================

	/**
	 * 提交意向留言
	 * @param dto
	 * @param ip
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitInquiry(InquirySubmitDTO dto, String ip) {
		long currentTime = System.currentTimeMillis();
		if (ipCooldownMap.getOrDefault(ip, 0L) > currentTime) {
			throw new BusinessException("提交过于频繁，请1分钟后再试");
		}
		if (inquiryMapper.countByIpToday(ip) >= 5) {
			throw new BusinessException("您今天的提交次数已达上限，请明天再试");
		}

		ipCooldownMap.put(ip, currentTime + 60000);

		Inquiry inquiry = new Inquiry();
		BeanUtils.copyProperties(dto, inquiry);
		inquiry.setCreateIp(ip);
		inquiry.setStatus(STATUS_WAIT);
		inquiryMapper.insert(inquiry);

		log.info("收到新留言: {} - IP: {}", dto.getApplicantName(), ip);
	}

	// ================== B端：管理接口 ==================

	/**
	 * 获取意向留言分页
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
	 * @param inquiryId
	 * @param handlerId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean assignHandler(Long inquiryId, Long handlerId) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) throw new BusinessException("记录不存在");
		checkTerminalState(inquiry);

		inquiry.setHandlerId(handlerId);
		inquiry.setStatus(STATUS_FOLLOWING);
		inquiry.setHandleTime(LocalDateTime.now());

		// 记录流水
		saveRecord(inquiryId, ACTION_ASSIGN, "指派负责人");

		return inquiryMapper.updateById(inquiry) > 0;
	}

	/**
	 * 跟进处理
	 * @param inquiryId
	 * @param result
	 * @param status
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean recordFollowUp(Long inquiryId, String result, Integer status) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) throw new BusinessException("记录不存在");
		checkTerminalState(inquiry);

		// 优化：直接调用 saveRecord 即可，删除了原先冗余的手动 insert 逻辑
		saveRecord(inquiryId, ACTION_FOLLOW, result);

		inquiry.setHandleResult(result);
		inquiry.setHandleTime(LocalDateTime.now());
		inquiry.setStatus(status);
		inquiry.setHandlerId(SecurityUtils.getUserId());

		return inquiryMapper.updateById(inquiry) > 0;
	}

	/**
	 * 转化为企业
	 * @param inquiryId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean convertToEnterprise(Long inquiryId) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) throw new BusinessException("记录不存在");
		checkTerminalState(inquiry);

		if (!StringUtils.hasText(inquiry.getCompanyName())) {
			throw new BusinessException("转化失败：未填写企业名称");
		}
		if (userMapper.selectByUsername(inquiry.getContactPhone()) != null) {
			throw new BusinessException("转化失败：该联系电话已存在系统账号");
		}

		// 1. 创建用户
		User newUser = new User();
		newUser.setUsername(inquiry.getContactPhone());
		newUser.setPassword(passwordEncoder.encode(inquiry.getContactPhone()));
		newUser.setRealName(inquiry.getApplicantName());
		newUser.setPhone(inquiry.getContactPhone());
		newUser.setStatus(1);
		userMapper.insert(newUser);

		// 2. 分配企业角色 (RoleID: 3)
		userRoleMapper.insertBatch(newUser.getId(), Collections.singletonList(3L));

		// 3. 初始化企业档案
		Enterprise enterprise = new Enterprise();
		enterprise.setUserId(newUser.getId());
		enterprise.setCompanyName(inquiry.getCompanyName());
		enterprise.setContactPerson(inquiry.getApplicantName());
		enterprise.setContactPhone(inquiry.getContactPhone());
		enterprise.setStatus(0);
		enterpriseMapper.insert(enterprise);

		// 4. 记录企业审计日志
		EnterpriseAudit auditLog = new EnterpriseAudit();
		auditLog.setEnterpriseId(enterprise.getId());
		auditLog.setStatus(0);
		auditLog.setOpinion("招商转化，系统自动初始化");
		auditLog.setAuditorId(SecurityUtils.getUserId());
		enterpriseAuditMapper.insert(auditLog);

		// 5. 更新留言主表状态
		inquiry.setStatus(STATUS_CONVERTED);
		inquiry.setHandleResult("已成功转化为入驻企业账号");
		inquiry.setHandleTime(LocalDateTime.now());
		inquiryMapper.updateById(inquiry);

		// 6. 补全留言流水记录 (关键修复：之前漏了这一步)
		saveRecord(inquiryId, ACTION_CONVERT, "意向成功转化为入驻企业");

		log.info("意向ID {} 成功转化为企业 {}", inquiryId, inquiry.getCompanyName());
		return true;
	}

	/**
	 * 删除意向留言
	 * @param inquiryId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteInquiry(Long inquiryId) {
		Inquiry inquiry = inquiryMapper.selectById(inquiryId);
		if (inquiry == null) throw new BusinessException("记录不存在");

		if (inquiry.getStatus() != STATUS_INVALID) {
			throw new BusinessException("只能删除【无效】状态的留言记录");
		}

		// 补全流水记录 (归档操作)
		saveRecord(inquiryId, ACTION_CLOSE, "执行彻底删除(逻辑删除)");

		inquiry.setIsDeleted(1);
		return inquiryMapper.updateById(inquiry) > 0;
	}

	// ================== 内部辅助方法 ==================

	/**
	 * 统一流水保存方法
	 * 确保 action_type 永远有值，解决数据库字段不能为空的问题
	 */
	private void saveRecord(Long inquiryId, Integer actionType, String content) {
		InquiryRecord record = new InquiryRecord();
		record.setInquiryId(inquiryId);
		record.setHandlerId(SecurityUtils.getUserId());
		record.setActionType(actionType); // 明确赋值 action_type
		record.setContent(content);
		inquiryRecordMapper.insert(record);
	}
}