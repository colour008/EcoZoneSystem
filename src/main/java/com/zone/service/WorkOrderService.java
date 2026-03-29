package com.zone.service;

import com.zone.domain.base.PageResult;
import com.zone.domain.dto.WorkOrderDTO;
import com.zone.domain.dto.WorkOrderEvaluateDTO;
import com.zone.domain.dto.WorkOrderPageQueryDTO;
import com.zone.domain.dto.WorkOrderProcessDTO;
import com.zone.domain.vo.WorkOrderStatsVO;
import com.zone.domain.vo.WorkOrderVO;

/**
 * @Author:     JamHoo
 * @Description:  工单业务接口
 * @Date:    2026/3/25 18:24
 * @Version:    1.0
 */
public interface WorkOrderService {

	// ================== C端：企业自助接口 ==================
	/**
	 * 提报工单
	 * @param dto
	 * @return
	 */
	boolean submit(WorkOrderDTO dto);

	/**
	 * 获取我的工单分页
	 * @param dto
	 * @return
	 */
	PageResult<WorkOrderVO> getMyPage(WorkOrderPageQueryDTO dto);

	/**
	 * 评价工单
	 * @param dto
	 * @return
	 */
	boolean evaluate(WorkOrderEvaluateDTO dto);

	// ================== B端：园区管控接口 ==================

	/**
	 * 分页查询工单列表
	 * @param dto
	 * @return
	 */
	PageResult<WorkOrderVO> getPage(WorkOrderPageQueryDTO dto);


	/**
	 * 分派工单
	 * @param orderId 工单ID
	 * @param workerId 处理人ID
	 * @return
	 */
	boolean dispatch(Long orderId, Long workerId);

	/**
	 * 处理反馈
	 * @param dto
	 * @return
	 */
	boolean process(WorkOrderProcessDTO dto);

	/**
	 * 获取处理中的工单分页
	 * @param dto
	 * @return
	 */
	PageResult<WorkOrderVO> getWorkerPage(WorkOrderPageQueryDTO dto);

	/**
	 * 根据ID查询工单
	 * @param id
	 * @return
	 */
	WorkOrderVO getById(Long id);

	/**
	 * 获取工单统计数据
	 * @return
	 */
	WorkOrderStatsVO getStatistics();
}
