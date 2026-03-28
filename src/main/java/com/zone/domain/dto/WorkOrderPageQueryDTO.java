package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 工单分页查询参数
 * @Date: 2026/3/27 21:12
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderPageQueryDTO extends PageQuery {

	/** 用户ID（C端查询时固定，B端查询时可选） */
	private Long userId;

	/** 工单编号（模糊搜索） */
	private String orderNo;

	/** 标题（模糊搜索） */
	private String title;

	/** 业务类型：1维修 2咨询 3投诉 */
	private Integer type;

	/** 状态：0待受理 1处理中 2已办结 3已评价 */
	private Integer status;

	/** 企业ID（C端查询时固定，B端查询时可选） */
	private Long enterpriseId;

	/** 受理专员ID（可选，用于查询某位专员派发的单子） */
	private Long handlerId;

	/** 处理人员ID（用于 H5 端查询分配给自己的工单） */
	private Long workerId;


	/** 开始时间（搜索创建时间范围） */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	/** 结束时间 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;
}
