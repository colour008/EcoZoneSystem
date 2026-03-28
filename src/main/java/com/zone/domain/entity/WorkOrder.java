package com.zone.domain.entity;

import com.zone.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 工单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrder extends BaseEntity {

	/** 工单编号 */
	private String orderNo;

	/** 企业ID */
	private Long enterpriseId;

	/** 标题 */
	private String title;

	/** 详细内容 */
	private String content;

	/** 报修/咨询联系人 */
	private String contactPerson;

	/** 联系电话 */
	private String contactPhone;

	/** 处理反馈/备注 */
	private String remark;

	/** 业务类型：1维修 2咨询 3投诉 */
	private Integer type;

	/** 状态：0待受理 1处理中 2已办结 3已评价 */
	private Integer status;

	/** 受理时间 */
	private LocalDateTime acceptTime;

	/** 完成时间 */
	private LocalDateTime finishTime;

	/** 受理人ID */
	private Long handlerId;

	/** 处理人ID */
	private Long workerId;

	/** 用户评价内容 */
	private String commentText;

	/** 评分(1-5星) */
	private Integer score;

	/** 提报附件图片URL,多张用逗号隔开 */
	private String images;

	/** 完成附件图片URL,多张用逗号隔开 */
	private String finishAttachments;

	/** 逻辑删除: 0正常 1删除 */
	private Integer isDeleted;

	private String enterpriseName; // 冗余字段：企业名称

	private String handlerName; // 冗余字段：受理人姓名

	private String workerName; // 冗余字段：处理人姓名

	/** 用户ID（C端查询时固定，B端查询时可选） */
	private Long userId;
}