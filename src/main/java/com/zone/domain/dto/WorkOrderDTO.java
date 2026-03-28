package com.zone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: JamHoo
 * @Description: 工单DTO
 * @Date: 2026/3/27 21:07
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDTO implements Serializable {

	private Long id;

	/** ------------------- C端提报字段 ------------------- */
	private Long userId;
	private String title;
	private String content;
	private String contactPerson;
	private String contactPhone;
	private Integer type;
	private String images; // 企业上传的报修图
	private String finishAttachments; // 工单处理完成后的附件

	/* ------------------- B端派单/处理字段 ------------------- */

	/** 执行工人ID（专员派单时传入） */
	private Long workerId;

	/** 处理反馈/备注（工人处理时填写） */
	private String remark;

	/** ------------------- C端评价字段 ------------------- */
	private String commentText;
	private Integer score;
}
