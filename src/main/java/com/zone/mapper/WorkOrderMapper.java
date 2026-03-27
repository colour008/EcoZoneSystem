package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.domain.dto.WorkOrderPageQueryDTO;
import com.zone.domain.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:     JamHoo
 * @Description: 工单映射接口
 * @Date:    2026/3/27 21:01
 * @Version:    1.0
 */
@Mapper
public interface WorkOrderMapper {

	/**
	 * 提报工单
	 * @param workOrder
	 * @return
	 */
	int insert(WorkOrder workOrder);

	/**
	 * 根据ID查询工单
	 * @param id
	 * @return
	 */
	WorkOrder selectById(Long id);

	/**
	 * 更新工单
	 * @param workOrder
	 * @return
	 */
	int updateById(WorkOrder workOrder);

	/**
	 * 分页查询工单列表
	 * @param dto
	 * @return
	 */
	Page<WorkOrder> getWorkOrderPage(WorkOrderPageQueryDTO dto);
}
