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
        // TODO: 1. 从Token获取当前登录人的enterpriseId
        // TODO: 2. 调用 CodeGenerator.getBusCode("WO") 生成单号
        boolean success = workOrderService.submit(dto);
        return success ? Result.success("工单已提交") : Result.sysError("提交失败");
    }

    @GetMapping("/my/page")
    @Operation(summary = "C端-获取我的工单分页")
    public Result<PageResult<WorkOrderVO>> getMyPage(WorkOrderPageQueryDTO dto) {
        // 自动注入当前企业的 ID
        return Result.success(workOrderService.getMyPage(dto));
    }

    @PutMapping("/evaluate")
    @Operation(summary = "C端-评价工单", description = "状态为已办结后可评价")
    public Result<String> evaluate(@RequestBody WorkOrderEvaluateDTO dto) {
        boolean success = workOrderService.evaluate(dto);
        return success ? Result.success("评价成功") : Result.sysError("操作失败");
    }


    // ================== B端：园区管控接口 ==================

    @GetMapping("/page")
    @Operation(summary = "B端-分页查询工单列表")
    public Result<PageResult<WorkOrderVO>> getPage(WorkOrderPageQueryDTO dto) {
        log.info("管理员查询工单列表, 状态: {}, 类型: {}", dto.getStatus(), dto.getType());
        return Result.success(workOrderService.getPage(dto));
    }

    @PutMapping("/accept/{id}")
    @Operation(summary = "B端-受理/分派工单", description = "修改状态为处理中，记录受理时间")
    public Result<String> accept(@PathVariable Long id, @RequestParam Long handlerId) {
        boolean success = workOrderService.accept(id, handlerId);
        return success ? Result.success("已受理") : Result.sysError("操作失败");
    }

    @PutMapping("/process")
    @Operation(summary = "B端-处理反馈", description = "填写备注信息，修改状态为已办结，记录完成时间")
    public Result<String> process(@RequestBody WorkOrderProcessDTO dto) {
        boolean success = workOrderService.process(dto);
        return success ? Result.success("处理完成") : Result.sysError("操作失败");
    }
}