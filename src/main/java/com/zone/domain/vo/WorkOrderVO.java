package com.zone.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 工单VO
 * @Date: 2026/3/27 21:09
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderVO {

	private Long id;
	private String orderNo;
	private Long enterpriseId;
	private String enterpriseName; // 冗余显示：企业名称

	private String title;
	private String content;
	private String contactPerson;
	private String contactPhone;

	private Integer type;
	private Integer status;

	private String remark;
	private Long handlerId;
	private String handlerName; // 冗余显示：受理人姓名
	private String workerName; // 冗余显示：处理人姓名

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime acceptTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime finishTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	private String commentText;
	private Integer score;
	private String images;

	/**
	 * 图片列表，用于前端直接渲染
	 */
	private List<String> imageList;

	// WorkOrderVO.java 中增加
	private List<String> finishAttachmentList;
}
