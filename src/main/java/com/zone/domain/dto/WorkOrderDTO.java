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

	private Long id; // 修改或处理时使用

	/** 用户ID */
	private Long userId;

	/** 标题 */
	private String title;

	/** 详细内容 */
	private String content;

	/** 报修/咨询联系人 */
	private String contactPerson;

	/** 联系电话 */
	private String contactPhone;

	/** 业务类型：1维修 2咨询 3投诉 */
	private Integer type;

	/** 附件图片，前端通常传逗号分隔的字符串 */
	private String images;

	// --- 以下字段通常由B端处理或C端评价时传入 ---

	private String remark;      // B端处理反馈
	private String commentText; // C端评价内容
	private Integer score;      // C端评分
}
