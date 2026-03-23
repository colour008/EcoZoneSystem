package com.zone.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 企业实体类 - 对应数据库 biz_enterprise
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enterprise extends BaseEntity {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long userId;            // 关联的系统用户ID
	private String companyName;     // 公司名称
	private String creditCode;      // 统一社会信用代码
	private String legalPerson;     // 法人代表
	private BigDecimal registeredCapital; // 注册资本(万元)
	private String buildingNo;      // 建筑编号
	private BigDecimal rentArea;    // 租用面积(㎡)
	private String industry;        // 行业
	private String introduction;     // 公司简介
	private String contactPerson;   // 企业联系人
	private String contactPhone;    // 联系方式
	private String licenseUrl;      // 营业执照附件路径
	private Integer status;         // 0:待审核 1:已入驻 2:驳回 3:迁出
	private String auditOpinion;    // 审核意见/驳回理由
	private Long auditorId;         // 审核人ID

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime auditTime; // 审核时间

	private LocalDate leaseStartDate; // 租约开始日期
	private LocalDate leaseEndDate;   // 租约结束日期
}