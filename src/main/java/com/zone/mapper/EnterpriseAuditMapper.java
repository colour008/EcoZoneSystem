package com.zone.mapper;

import com.zone.domain.entity.EnterpriseAudit;
import com.zone.domain.vo.EnterpriseAuditVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnterpriseAuditMapper {
	/**
	 * 插入流水记录
	 */
	int insert(EnterpriseAudit auditLog);

	/**
	 * 根据企业ID查询审核流水历史 (含审核人姓名)
	 */
	List<EnterpriseAuditVO> selectByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
}
