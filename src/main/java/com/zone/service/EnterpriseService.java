package com.zone.service;

import com.zone.domain.base.PageResult;
import com.zone.domain.dto.EnterpriseDTO;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.entity.EnterpriseAudit;
import com.zone.domain.vo.EnterpriseAuditVO;
import com.zone.domain.vo.EnterpriseVO;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 企业服务接口
 * @Date: 2026/3/21 14:22
 * @Version: 1.0
 */
public interface EnterpriseService {

	// ================== C端自助接口 ==================
	/**
	 * 提交入驻申请
	 *
	 * @param enterpriseDTO
	 * @return
	 */
	boolean apply(EnterpriseDTO enterpriseDTO);

	/**
	 * 获取当前用户入驻信息
	 *
	 * @return
	 */
	EnterpriseVO getMyEnterprise();

	/**
	 * 修改入驻信息
	 *
	 * @param enterpriseDTO
	 * @return
	 */
	boolean updateMyEnterprise(EnterpriseDTO enterpriseDTO);


	// ================== B端管控接口 ==================

	/**
	 * 获取所有企业列表
	 *
	 * @return
	 */
	List<EnterpriseVO> listAll();

	/**
	 * 审核入驻申请
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	boolean audit(Long id, Integer status, String auditOpinion);

	/**
	 * 分页查询企业列表
	 *
	 * @param dto
	 * @return
	 */
	PageResult<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto);

	/**
	 * 获取详情
	 *
	 * @param id
	 * @return
	 */
	EnterpriseVO getDetailById(Long id);

	/**
	 * 迁出
	 *
	 * @param id
	 * @return
	 */
	boolean moveOut(Long id);

	/**
	 * 修改
	 *
	 * @param enterpriseDTO
	 * @return
	 */
	boolean updateEnterprise(EnterpriseDTO enterpriseDTO);

	/**
	 * 删除
	 *
	 * @param id
	 * @return
	 */
	boolean deleteById(Long id);

	/**
	 * 获取审核历史
	 *
	 * @param id
	 * @return
	 */
	List<EnterpriseAuditVO> getAuditHistory(Long id);
}
