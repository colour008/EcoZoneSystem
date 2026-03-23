package com.zone.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 企业视图对象 - 增强版，支持入驻与迁出双重逻辑
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseVO extends BaseEntity {
	private Long id;
	private Long userId;
	private String companyName;
	private String creditCode;
	private String legalPerson;
	private BigDecimal registeredCapital;
	private String buildingNo;
	private BigDecimal rentArea;
	private String industry;
	private String introduction;
	private String contactPerson;
	private String contactPhone;
	private String licenseUrl;

	/**
	 * 企业状态：0-入驻待审, 1-已入驻, 2-入驻驳回, 3-已迁出, 4-迁出待审
	 */
	private Integer status;

	/**
	 * 最新审核意见（管理员填写的结论）
	 */
	private String auditOpinion;

	/**
	 * 申请理由（重点：从流水表中 status=0 或 status=4 的记录中提取的 opinion）
	 */
	private String applyReason;

	private Long auditorId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime auditTime;

	private LocalDate leaseStartDate;
	private LocalDate leaseEndDate;

	// 关联查询出来的扩展名称
	private String userName;
	private String applicantName; // 申请人真实姓名/用户名
	private String auditorName;   // 审核人真实姓名
}