package com.zone.domain.vo;

import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: 企业VO
 * @Date: 2026/3/21 14:18
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseVO extends BaseEntity {
	private Long userId;        // 关联的系统用户ID
	private String userName;        // 关联的系统用户姓名
	private String companyName;  // 公司名称
	private String creditCode;  // 统一社会信用代码
	private String buildingNo;  // 建筑编号
	private String industry;  // 行业
	private String contactPerson; // 联系人
	private String contactPhone; // 联系方式
	private Integer status;     // 0:待审核 1:已入驻 2:驳回 3:迁出
}
