package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.WorkOrderDTO;
import com.zone.domain.dto.WorkOrderEvaluateDTO;
import com.zone.domain.dto.WorkOrderPageQueryDTO;
import com.zone.domain.dto.WorkOrderProcessDTO;
import com.zone.domain.vo.WorkOrderVO;
import com.zone.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@Tag(name = "工单管理接口")
@RequestMapping("/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    // ================== C端：企业自助接口 ==================

    @PostMapping("/apply")
    @Operation(summary = "C端-提报工单")
    public Result<String> submit(@RequestBody WorkOrderDTO dto) {
        log.info("企业提报工单: {}", dto.getTitle());
        boolean success = workOrderService.submit(dto);
        return success ? Result.success("工单已提交") : Result.sysError("提交失败");
    }

    @GetMapping("/my/page")
    @Operation(summary = "C端-获取我的工单分页")
    public Result<PageResult<WorkOrderVO>> getMyPage(WorkOrderPageQueryDTO dto) {
        return Result.success(workOrderService.getMyPage(dto));
    }

    @PutMapping("/evaluate")
    @Operation(summary = "C端-评价工单")
    public Result<String> evaluate(@RequestBody WorkOrderEvaluateDTO dto) {
        boolean success = workOrderService.evaluate(dto);
        return success ? Result.success("评价成功") : Result.sysError("操作失败");
    }


    // ================== B端：园区管控接口 (PC管理端) ==================

    @GetMapping("/page")
    @Operation(summary = "B端-分页查询工单列表")
    public Result<PageResult<WorkOrderVO>> getPage(WorkOrderPageQueryDTO dto) {
        return Result.success(workOrderService.getPage(dto));
    }

    /**
     * 接收参数：orderId (工单ID), workerId (选中的工人ID)
     */
    @PutMapping("/dispatch")
    @Operation(summary = "B端-委派工单", description = "专员将工单指派给具体的执行工人")
    public Result<String> dispatch(@RequestBody Map<String, Long> params) {
        Long orderId = params.get("orderId");
        Long workerId = params.get("workerId");
        if (orderId == null || workerId == null) {
            return Result.sysError("参数错误，请选择执行人");
        }
        boolean success = workOrderService.dispatch(orderId, workerId);
        return success ? Result.success("委派成功") : Result.sysError("操作失败");
    }


    // ================== H5端：工人执行接口 (移动端) ==================

    /**
     * 新增：供工人在 H5 端查看分配给自己的任务
     */
    @GetMapping("/worker/page")
    @Operation(summary = "H5端-获取工人自己的待办工单")
    public Result<PageResult<WorkOrderVO>> getWorkerPage(WorkOrderPageQueryDTO dto) {
        // 在 Service 层会通过 SecurityContext 获取当前登录工人的 workerId
        return Result.success(workOrderService.getWorkerPage(dto));
    }

    @PutMapping("/process")
    @Operation(summary = "B端/H5端-处理反馈", description = "工人提交处理结果和凭证，状态变为已办结")
    public Result<String> process(@RequestBody WorkOrderProcessDTO dto) {
        log.info("工人处理工单反馈: {}", dto.getId());
        boolean success = workOrderService.process(dto);
        return success ? Result.success("处理完成") : Result.sysError("操作失败");
    }
}