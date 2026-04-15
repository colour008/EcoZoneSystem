package com.zone.domain.entity;

import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 意向留言实体类 - 对应数据库 biz_inquiry
 * @Date: 2026-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Inquiry extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String applicantName;  // 申请人/联系人姓名
	private String companyName;    // 企业名称(选填)
	private String contactPhone;   // 联系电话
	private Integer type;          // 意向类型: 1入驻咨询 2求职对接 3服务需求 4一般留言
	private String remark;         // 留言详情
	private Integer status;        // 处理状态: 0待处理 1跟进中 2已转入驻 3已完结 4无效
	private Long handlerId;        // 跟进人ID(关联sys_user)
	private LocalDateTime handleTime; // 最新处理时间
	private String handleResult;   // 处理结果/跟进记录
	private String createIp;       // 提交者IP(用于防刷)
	private Integer isDeleted;     // 逻辑删除: 0正常 1删除
}