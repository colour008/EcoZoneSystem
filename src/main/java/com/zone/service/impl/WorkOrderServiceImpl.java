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
	@Transactional(rollbackFor = Exception.class) // 1. 涉及单号生成和入库，建议开启事务
	public boolean submit(WorkOrderDTO dto) {
		// 2. 强制校验企业身份
		Long enterpriseId = SecurityUtils.getEnterpriseId();
		if (enterpriseId == null) {
			throw new BusinessException(ResponseCodeEnum.NOT_ENTERPRISE_USER);
		}

		// 3. 基础参数简单校验
		if (StringUtils.isBlank(dto.getTitle()) || dto.getType() == null) {
			throw new BusinessException(ResponseCodeEnum.PARAM_ERROR);
		}

		WorkOrder workOrder = new WorkOrder();
		BeanUtils.copyProperties(dto, workOrder);

		// 4. 核心字段强制重置 (覆盖 DTO 可能带来的恶意篡改)
		workOrder.setEnterpriseId(enterpriseId);
		workOrder.setOrderNo(CodeGenerator.getBusCode("WO")); // 生成唯一单号
		workOrder.setStatus(0); // 初始状态强制为：待受理

		// 5. 初始化一些 DTO 里不该有的字段，确保数据干净
		workOrder.setHandlerId(null);
		workOrder.setAcceptTime(null);
		workOrder.setFinishTime(null);
		workOrder.setIsDeleted(0);

		log.info("企业[{}]提交了新工单: {}", enterpriseId, workOrder.getOrderNo());

		return workOrderMapper.insert(workOrder) > 0;
	}

	/**
	 * 获取我的工单分页
	 * @param dto
	 * @return
	 */
	@Override
	public PageResult<WorkOrderVO> getMyPage(WorkOrderPageQueryDTO dto) {
		// C端查询强制锁定当前企业ID
		dto.setEnterpriseId(SecurityUtils.getEnterpriseId());
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

		// 只有已办结(2)的工单才能评价
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
		workOrder.setStatus(1); // 处理中

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
		workOrder.setStatus(2); // 已办结

		return workOrderMapper.updateById(workOrder) > 0;
	}

	/**
	 * 通用分页查询提取
	 */
	private PageResult<WorkOrderVO> getPageResult(WorkOrderPageQueryDTO dto) {
		// 1. 开启分页
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());

		// 2. 查询
		Page<WorkOrder> page = workOrderMapper.getWorkOrderPage(dto);

		// 3. 转换为 VO 并手动处理图片列表
		List<WorkOrderVO> voList = page.getResult().stream().map(workOrder -> {
			WorkOrderVO vo = new WorkOrderVO();
			BeanUtils.copyProperties(workOrder, vo);

			// --- 手动处理图片逻辑 ---
			String imagesStr = workOrder.getImages();
			if (imagesStr != null && !imagesStr.trim().isEmpty()) {
				// 将字符串按逗号拆分并转为 List
				List<String> list = Arrays.stream(imagesStr.split(","))
						.map(String::trim) // 去空格
						.filter(s -> !s.isEmpty()) // 过滤空串
						.collect(Collectors.toList());
				vo.setImageList(list);
			} else {
				vo.setImageList(new ArrayList<>()); // 返回空集合而非 null，防止前端报错
			}
			// -----------------------

			return vo;
		}).collect(Collectors.toList());

		return new PageResult<>(page.getTotal(), voList);
	}
}
