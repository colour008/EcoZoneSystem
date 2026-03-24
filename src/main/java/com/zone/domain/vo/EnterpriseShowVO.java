package com.zone.domain.vo;

import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 企业风采展示VO
 * @Date: 2026/3/24 19:41
 * @Version: 1.0
 */
@Data
public class EnterpriseShowVO {
	private Long id;
	private String companyName;    // 企业名称
	private String industry;       // 所属行业
	private String buildingNo;     // 楼宇位置
	private String introduction;   // 企业简介
	private String contactPerson;  // 联系人
	private String contactPhone;   // 联系电话
}
