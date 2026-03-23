package com.zone.domain.vo;

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
 * @Description: 企业视图对象 - 用于前端展示
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseVO extends BaseEntity {
	private Long id;
	private Long userId;
	private String userName;
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
	private Integer status;
	private String auditOpinion;

	private Long auditorId;
	private LocalDateTime auditTime;
	private LocalDate leaseStartDate;
	private LocalDate leaseEndDate;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;

	// 关联查询出来的名称
	private String applicantName;
	private String auditorName;
}