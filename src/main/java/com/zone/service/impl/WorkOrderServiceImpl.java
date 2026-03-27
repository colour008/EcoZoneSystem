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
		// 1. 规范化：从数据库实时查询用户绑定的企业ID，不相信前端传参
		Long enterpriseId = workOrderMapper.getEnterpriseIdByUserId(userId);
		if (enterpriseId == null) {
			throw new BusinessException(ResponseCodeEnum.NOT_ENTERPRISE_USER);
		}

		WorkOrder workOrder = new WorkOrder();
		BeanUtils.copyProperties(dto, workOrder);

		// 2. 强制绑定后端查出来的企业ID
		workOrder.setEnterpriseId(enterpriseId);
		workOrder.setOrderNo(CodeGenerator.getBusCode("WO"));
		workOrder.setStatus(0); // 待受理
		workOrder.setCreateTime(LocalDateTime.now());
		workOrder.setIsDeleted(0);

		return workOrderMapper.insert(workOrder) > 0;
	}

	/**
	 * 获取我的工单分页
	 * @param dto
	 * @return
	 */
	@Override
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
		workOrder.setStatus(3); // 已评价
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
		return getPageResult(dto);
	}

	/**
	 * 受理/分派工单
	 * @param id
	 * @param handlerId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean accept(Long id, Long handlerId) {
		WorkOrder workOrder = workOrderMapper.selectById(id);
		if (workOrder == null || workOrder.getStatus() != 0) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_STATUS_ERROR);
		}
		workOrder.setHandlerId(handlerId);
		workOrder.setAcceptTime(LocalDateTime.now());
		workOrder.setStatus(1);
		return workOrderMapper.updateById(workOrder) > 0;
	}

	/**
	 * 处理反馈
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean process(WorkOrderProcessDTO dto) {
		WorkOrder workOrder = workOrderMapper.selectById(dto.getId());
		if (workOrder == null || workOrder.getStatus() != 1) {
			throw new BusinessException(ResponseCodeEnum.WORK_ORDER_STATUS_ERROR);
		}
		workOrder.setRemark(dto.getRemark());
		workOrder.setFinishTime(LocalDateTime.now());
		workOrder.setStatus(2);
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
