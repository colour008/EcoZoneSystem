package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: JamHoo
 * @Description: 企业分页查询参数
 * @Date: 2026/3/21 17:30
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data

public class EnterprisePageQueryDTO extends PageQuery {
	private Long userId;        // 关联的系统用户ID
	private String userName;  // 关联的系统用户姓名
	private String companyName;  // 公司名称
	private String creditCode;  // 统一社会信用代码
	private String buildingNo;  // 建筑编号
	private String industry;  // 行业
	private String contactPerson; // 联系人
	private Integer status;     // 0:待审核 1:已入驻 2:驳回 3:迁出
}
