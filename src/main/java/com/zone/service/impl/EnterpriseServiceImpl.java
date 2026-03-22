package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.EnterpriseDTO;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.entity.Enterprise;
import com.zone.domain.entity.EnterpriseAudit;
import com.zone.domain.vo.EnterpriseAuditVO;
import com.zone.domain.vo.EnterpriseVO;
import com.zone.mapper.EnterpriseAuditMapper;
import com.zone.mapper.EnterpriseMapper;
import com.zone.service.EnterpriseService;
import com.zone.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author: JamHoo
 * @Description: 企业服务实现类
 * @Date: 2026/3/21 14:22
 * @Version: 1.0
 */
@Service
@Slf4j
public class EnterpriseServiceImpl implements EnterpriseService {
	@Autowired
	private EnterpriseMapper enterpriseMapper;
	@Autowired
	private EnterpriseAuditMapper enterpriseAuditMapper;

	// ================== C端自助接口 ==================
	/**
	 * 提交入驻申请
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean apply(EnterpriseDTO enterpriseDTO) {
		Long currentUserId = SecurityUtils.getUserId();

		// 1. 查询该用户是否已有入驻申请记录
		// 注意：这里需要你在 EnterpriseMapper 中新增一个 selectByUserId 的方法，或者直接用 listAll 过滤
		List<Enterprise> list = enterpriseMapper.listAllByUserId(currentUserId);
		Enterprise existing = (list != null && !list.isEmpty()) ? list.get(0) : null;

		if (existing == null) {
			// --- 场景 A：初次提交 ---
			checkCreditCodeUnique(enterpriseDTO.getCreditCode(), null);

			Enterprise enterprise = new Enterprise();
			BeanUtils.copyProperties(enterpriseDTO, enterprise);
			enterprise.setUserId(currentUserId);
			enterprise.setStatus(0); // 待审核

			boolean saved = enterpriseMapper.insert(enterprise) > 0;
			if (saved) {
				// 记录初始流转日志
				saveAuditRecord(enterprise.getId(), 0, "用户发起初次入驻申请", currentUserId);
			}
			return saved;
		} else {
			// --- 场景 B：重新提交 (驳回或迁出后) ---

			// 逻辑校验：只有【已驳回(2)】或【已迁出(3)】才允许重新申请
			if (existing.getStatus() == 0) {
				throw new BusinessException("申请正在审核中，请勿重复提交");
			}
			if (existing.getStatus() == 1) {
				throw new BusinessException("企业已在园区入驻，如需变更请联系管理员");
			}

			// 唯一性校验需排除自身 ID
			checkCreditCodeUnique(enterpriseDTO.getCreditCode(), existing.getId());

			// 更新旧记录
			BeanUtils.copyProperties(enterpriseDTO, existing);
			existing.setId(existing.getId()); // 确保 ID 不丢失
			existing.setStatus(0);            // 状态：待审核
			existing.setAuditOpinion(null);   // 必须清空！否则前端会因为有意见而显示“驳回”提示
			existing.setAuditorId(null);      // 清空审核人
			existing.setAuditTime(null);      // 清空审核时间
			existing.setCreateTime(LocalDateTime.now());

			boolean updated = enterpriseMapper.updateById(existing) > 0;
			if (updated) {
				// 记录流水：这步很重要，前端时间轴会显示这一条
				saveAuditRecord(existing.getId(), 0, "用户修改资料，重新发起入驻申请", currentUserId);
			}
			return updated;
		}
	}

	/**
	 * 提取出的私有方法：保存审核流转记录
	 */
	private void saveAuditRecord(Long enterpriseId, Integer status, String opinion, Long operatorId) {
		EnterpriseAudit auditLog = new EnterpriseAudit();
		auditLog.setEnterpriseId(enterpriseId);
		auditLog.setStatus(status);
		auditLog.setOpinion(opinion);
		auditLog.setAuditorId(operatorId);
		enterpriseAuditMapper.insert(auditLog);
	}

	/**
	 * 获取企业的入驻信息
	 *
	 * @return
	 */
	@Override
	public EnterpriseVO getMyEnterprise() {
		Long currentUserId = SecurityUtils.getUserId();
		// 1. 调用 Mapper 查询该用户关联的企业
		List<Enterprise> list = enterpriseMapper.listAllByUserId(currentUserId);

		if (list == null || list.isEmpty()) {
			return null; // 返回空，前端会根据这个显示“立即申请”界面
		}

		// 2. 取最新的一条记录（通常一个用户对应一家企业）
		Enterprise entity = list.get(0);

		// 3. 转换成 VO，VO 包含了 applicant_name 等扩展字段
		EnterpriseVO vo = new EnterpriseVO();
		BeanUtils.copyProperties(entity, vo);

		// 如果需要显示审核人的真实姓名，可以调用 getById (带 Join 查询的那个)
		return getDetailById(entity.getId());
	}

	/**
	 * 修改入驻信息
	 *
	 * @param enterpriseDTO
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateMyEnterprise(EnterpriseDTO enterpriseDTO) {
		return false;
	}

	// ================== B端管控接口 ==================
	/**
	 * 获取所有企业列表
	 *
	 * @return
	 */
	@Override
	public List<EnterpriseVO> listAll() {
		// 1. 从 Mapper 获取 Entity 列表
		List<Enterprise> entities = enterpriseMapper.listAll();

		// 2. 转换成 VO 列表 (利用 BeanUtils)
		return entities.stream().map(entity -> {
			EnterpriseVO vo = new EnterpriseVO();
			BeanUtils.copyProperties(entity, vo);
			return vo;
		}).collect(Collectors.toList());
	}

	/**
	 * 获取企业分页列表
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public PageResult<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto) {
		// 1. 开启分页
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());

		// 2. 直接获取 VO 类型的 Page 对象
		// 此时 MyBatis 返回的就是 List<EnterpriseVO>，类型完全匹配
		Page<EnterpriseVO> page = enterpriseMapper.getEnterprisePage(dto);

		// 3. 直接封装返回
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	/**
	 * 审核入驻申请
	 *
	 * @param id     企业ID
	 * @param status 1:通过, 2:驳回
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean audit(Long id, Integer status, String auditOpinion) {
		Long currentAdminId = SecurityUtils.getUserId();
		log.info("执行审核操作: 企业ID={}, 状态={}, 理由={}", id, status, auditOpinion);

		// 1. 更新主表状态
		int updated = enterpriseMapper.updateAuditStatus(id, status, auditOpinion, currentAdminId);
		if (updated <= 0) return false;

		// 2. 记录审核流水 (复用私有方法)
		saveAuditRecord(id, status, auditOpinion, currentAdminId);

		return true;
	}

	/**
	 * 获取企业详情
	 *
	 * @param id
	 * @return
	 */
	@Override
	public EnterpriseVO getDetailById(Long id) {
		if (id == null) return null;
		// 使用带有 sys_user 关联查询的 Mapper 方法
		return enterpriseMapper.getById(id);
	}



	/**
	 * 迁出企业
	 *
	 * @param id
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean moveOut(Long id) {
		Long currentAdminId = SecurityUtils.getUserId();
		// 状态 3 代表已迁出
		int rows = enterpriseMapper.updateAuditStatus(id, 3, "企业办理迁出手续", currentAdminId);
		if (rows > 0) {
			saveAuditRecord(id, 3, "管理员办理企业迁出", currentAdminId);
		}
		return rows > 0;
	}

	@Override
	public boolean updateEnterprise(EnterpriseDTO enterpriseDTO) {
		// 1. 基础校验
		if (enterpriseDTO.getId() == null) {
			throw new BusinessException(ResponseCodeEnum.PARAM_ERROR);
		}

		// 2. 权限校验
		if (!SecurityUtils.isAdmin() && !SecurityUtils.getRoleCodes().contains("ROLE_STAFF")) {
			throw new BusinessException(ResponseCodeEnum.PERMISSION_DENIED);
		}

		// 3. 唯一性校验：传入当前 DTO 的 creditCode 和 ID
		// 这样如果用户没改信用代码，SQL 会执行: WHERE credit_code = '原值' AND id != '原ID'，结果为 0，通过校验
		checkCreditCodeUnique(enterpriseDTO.getCreditCode(), enterpriseDTO.getId());

		// 4. 执行更新
		Enterprise enterprise = new Enterprise();
		BeanUtils.copyProperties(enterpriseDTO, enterprise);

		log.info("管理员 {} 正在修改企业 ID: {}", SecurityUtils.getUsername(), enterpriseDTO.getId());

		// 原生 MyBatis 通常返回受影响行数
		int rows = enterpriseMapper.updateById(enterprise);
		return rows > 0;
	}


	/**
	 * 删除企业 (支持批量)
	 *
	 * @param ids 企业ID列表
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}
		// 1. 先删除关联的审核流水记录
		enterpriseAuditMapper.deleteByEnterpriseIds(ids);

		// 2. 删除主表记录
		return enterpriseMapper.deleteByIds(ids) > 0;
	}

	/**
	 * 获取审核历史
	 *
	 * @param id
	 * @return
	 */
	@Override
	public List<EnterpriseAuditVO> getAuditHistory(Long id) {
		if (id == null) {
			return new ArrayList<>();
		}
		// 原生 MyBatis 直接调用接口
		return enterpriseAuditMapper.selectByEnterpriseId(id);
	}

	/**
	 * 校验信用代码唯一性
	 */
	private void checkCreditCodeUnique(String creditCode, Long excludeId) {
		// 强制处理：如果是新增，ID 可能是 0 或空，统一视为 null
		if (excludeId != null && excludeId <= 0) {
			excludeId = null;
		}
		if (creditCode == null || creditCode.trim().isEmpty()) {
			return;
		}

		// 调用 Mapper 查询数据库中是否存在冲突
		int count = enterpriseMapper.countByCreditCode(creditCode, excludeId);

		if (count > 0) {
			// 这里抛出你的业务异常
			throw new BusinessException(ResponseCodeEnum.ENTERPRISE_CREDIT_CODE_DUPLICATE);
		}
	}

	/**
	 * 获取待审核数量
	 *
	 * @return
	 */
	@Override
	public int getPendingCount() {
		return enterpriseMapper.countPendingApplications();
	}

}
