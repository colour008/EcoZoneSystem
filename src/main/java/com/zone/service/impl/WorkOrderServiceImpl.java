package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.WorkOrderDTO;
import com.zone.domain.dto.WorkOrderEvaluateDTO;
import com.zone.domain.dto.WorkOrderPageQueryDTO;
import com.zone.domain.dto.WorkOrderProcessDTO;
import com.zone.domain.entity.WorkOrder;
import com.zone.domain.vo.WorkOrderVO;
import com.zone.mapper.WorkOrderMapper;
import com.zone.service.WorkOrderService;
import com.zone.util.CodeGenerator;
import com.zone.util.SecurityUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: JamHoo
 * @Description: 工单业务层实现类
 * @Date: 2026/3/27 21:01
 * @Version: 1.0
 */

@Service
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

	@Autowired
	private WorkOrderMapper workOrderMapper;

	// ================== C端：企业自助接口 ==================
	/**
	 * 提报工单
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean submit(WorkOrderDTO dto) {
		Long userId = SecurityUtils.getUserId();
		Long enterpriseId = workOrderMapper.getEnterpriseIdByUserId(userId);
		if (enterpriseId == null) {
			throw new BusinessException(ResponseCodeEnum.NOT_ENTERPRISE_USER);
		}

		WorkOrder workOrder = new WorkOrder();
		BeanUtils.copyProperties(dto, workOrder);

		workOrder.setEnterpriseId(enterpriseId);
		workOrder.setOrderNo(CodeGenerator.getBusCode("WO"));
		workOrder.setStatus(0);
		workOrder.setCreateTime(LocalDateTime.now());
		workOrder.setIsDeleted(0);

		return workOrderMapper.insert(workOrder) > 0;
	}

	/**
	 * 获取我的工单分页
	 * @param dto
	 * @return
	 */
	public PageResult<WorkOrderVO> getMyPage(WorkOrderPageQueryDTO dto) {
		Long userId = SecurityUtils.getUserId();
		if (userId == null) {
			return new PageResult<>(0L, new ArrayList<>());
		}
		dto.setUserId(userId);
		return getPageResult(dto);
	}

	/**
	 * 评价工单
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean evaluate(WorkOrderEvaluateDTO dto) {
		WorkOrder workOrder = workOrderMapper.selectById(dto.getId());
		if (workOrder == null) throw new BusinessException(ResponseCodeEnum.WORK_ORDER_NOT_EXIST);

		Long myEntId = workOrderMapper.getEnterpriseIdByUserId(SecurityUtils.getUserId());
		if (!workOrder.getEnterpriseId().equals(myEntId)) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_NO_PERMISSION);
		}

		if (workOrder.getStatus() != 2) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_EVALUATE_FAILED);
		}

		workOrder.setScore(dto.getScore());
		workOrder.setCommentText(dto.getCommentText());
		workOrder.setStatus(3);
		return workOrderMapper.updateById(workOrder) > 0;
	}

	// ================== B端：园区管控接口 ==================

	/**
	 * 分页查询工单列表
	 * @param dto
	 * @return
	 */
	@Override
	public PageResult<WorkOrderVO> getPage(WorkOrderPageQueryDTO dto) {
		// ===================== 权限过滤 =====================
		Long currentUserId = SecurityUtils.getUserId();
		// 判断当前用户是否为 维修/工作人员角色
		boolean isWorker = SecurityUtils.getRoleCodes().contains("ROLE_WORKER");
		// 工作人员：仅查看分配给自己的工单
		if (isWorker) {
			dto.setWorkerId(currentUserId);
		}
		// 超级管理员/园区管理员：不设置workerId，查看全部工单
		return getPageResult(dto);
	}

	/**
	 * 核心修改：分派工单
	 * @param orderId 工单ID
	 * @param workerId 执行工人ID (ROLE_WORKER)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean dispatch(Long orderId, Long workerId) {
		WorkOrder workOrder = workOrderMapper.selectById(orderId);
		if (workOrder == null || workOrder.getStatus() != 0) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_STATUS_ERROR);
		}

		// 记录谁派的单 (当前登录专员)
		workOrder.setHandlerId(SecurityUtils.getUserId());
		// 记录谁去干活 (选中的工人)
		workOrder.setWorkerId(workerId);

		workOrder.setAcceptTime(LocalDateTime.now());
		workOrder.setStatus(1); // 变为处理中
		return workOrderMapper.updateById(workOrder) > 0;
	}

	// ================== H5端：工人执行接口 ==================

	/**
	 * 获取分配给当前工人的工单列表
	 */
	@Override
	public PageResult<WorkOrderVO> getWorkerPage(WorkOrderPageQueryDTO dto) {
		Long currentWorkerId = SecurityUtils.getUserId();
		// 强制过滤只能看到自己的任务
		dto.setWorkerId(currentWorkerId);
		// 通常工人只关心待处理(status=1)的任务，如果前端没传状态，可以在此设置默认
		if (dto.getStatus() == null) {
			dto.setStatus(1);
		}
		return getPageResult(dto);
	}

	/**
	 * 工人处理反馈
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean process(WorkOrderProcessDTO dto) {
		WorkOrder workOrder = workOrderMapper.selectById(dto.getId());
		if (workOrder == null || workOrder.getStatus() != 1) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_STATUS_ERROR);
		}

		// 安全校验：非管理员只能处理分配给自己的单子
		Long currentUserId = SecurityUtils.getUserId();
		if (!workOrder.getWorkerId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_NO_PERMISSION);
		}

		workOrder.setRemark(dto.getRemark());
		// 如果工人上传了处理凭证图
		if (StringUtils.isNotBlank(dto.getImages())) {
			workOrder.setImages(dto.getImages());
		}

		workOrder.setFinishTime(LocalDateTime.now());
		workOrder.setStatus(2); // 变为已办结
		return workOrderMapper.updateById(workOrder) > 0;
	}

	/**
	 * 通用分页查询提取
	 */
	private PageResult<WorkOrderVO> getPageResult(WorkOrderPageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<WorkOrder> page = workOrderMapper.getWorkOrderPage(dto);
		List<WorkOrderVO> voList = page.getResult().stream().map(this::convertToVO).collect(Collectors.toList());
		return new PageResult<>(page.getTotal(), voList);
	}

	/**
	 * 工单实体转VO
	 */
	private WorkOrderVO convertToVO(WorkOrder workOrder) {
		WorkOrderVO vo = new WorkOrderVO();
		BeanUtils.copyProperties(workOrder, vo);
		if (StringUtils.isNotBlank(workOrder.getImages())) {
			vo.setImageList(Arrays.asList(workOrder.getImages().split(",")));
		} else {
			vo.setImageList(new ArrayList<>());
		}
		return vo;
	}
}
