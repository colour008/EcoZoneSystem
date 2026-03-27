package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Author: JamHoo
 * @Description: 企业分页查询参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnterprisePageQueryDTO extends PageQuery implements Serializable {
	private String companyName;     // 公司名称模糊查询
	private String creditCode;      // 信用代码
	private String industry;        // 所属行业
	private String buildingNo;      // 所在楼宇
	private Integer status;         // 状态
	private String contactPerson;   // 联系人

	// 扩展查询字段：入驻日期范围
	private LocalDate beginLeaseDate;
	private LocalDate endLeaseDate;
}