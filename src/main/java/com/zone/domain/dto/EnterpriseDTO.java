package com.zone.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author: JamHoo
 * @Description: 企业入驻/更新参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseDTO {
	private Long id;                // 主键ID
	private String companyName;     // 公司名称
	private String creditCode;      // 统一社会信用代码
	private String legalPerson;     // 法人代表
	private BigDecimal registeredCapital; // 注册资本
	private String buildingNo;      // 建筑编号
	private BigDecimal rentArea;    // 租用面积
	private String industry;        // 行业
	@Size(max = 20000, message = "企业介绍内容过长")
	private String introduction;     // 公司简介
	private String contactPerson;   // 企业联系人
	private String contactPhone;    // 联系方式
	private String licenseUrl;      // 营业执照附件（文件上传后的路径）
	private Integer status;         // 0:待审核 1:已入驻 2:驳回 3:迁出

	// 审核意见通常在 B 端审核接口单独传，但为了通用也可以保留
	private String auditOpinion;
	private LocalDate leaseStartDate;
	private LocalDate leaseEndDate;
}