package com.zone.domain.vo;

import com.zone.domain.entity.EnterpriseAudit;
import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 审核流水VO
 * @Date: 2026/3/22 13:31
 * @Version: 1.0
 */
@Data
public class EnterpriseAuditVO extends EnterpriseAudit {
	/**
	 * 审核人真实姓名 (从 sys_user 表关联)
	 */
	private String auditorName;
}
