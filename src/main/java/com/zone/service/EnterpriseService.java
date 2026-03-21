package com.zone.service;

import com.zone.domain.base.PageResult;
import com.zone.domain.dto.EnterpriseDTO;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.vo.EnterpriseVO;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 企业服务接口
 * @Date: 2026/3/21 14:22
 * @Version: 1.0
 */
public interface EnterpriseService {

	/**
	 * 获取所有企业列表
	 *
	 * @return
	 */
	List<EnterpriseVO> listAll();



	/**
	 * 提交入驻申请
	 *
	 * @param enterpriseDTO
	 * @return
	 */
	boolean apply(EnterpriseDTO enterpriseDTO);

	/**
	 * 审核入驻申请
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	boolean audit(Long id, Integer status);

	/**
	 * 分页查询企业列表
	 *
	 * @param dto
	 * @return
	 */
	PageResult<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto);
}
