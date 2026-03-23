package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.EnterpriseDTO;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.entity.EnterpriseAudit;
import com.zone.domain.vo.EnterpriseAuditVO;
import com.zone.domain.vo.EnterpriseVO;
import com.zone.service.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/enterprise")
@Tag(name = "企业管理接口", description = "涵盖B端管控与C端自助的入驻业务")
public class EnterpriseController {

	@Autowired
	private EnterpriseService enterpriseService;

	// ================== C端自助接口 ==================

	@PostMapping("/apply")
	@Operation(summary = "C端-提交入驻申请")
	public Result<String> apply(@RequestBody EnterpriseDTO enterpriseDTO) {
		// TODO: Service 层需从 Token 获取 currentUserId 并校验是否已存在申请
		log.info("提交入驻申请: {}", enterpriseDTO.getCompanyName());
		boolean success = enterpriseService.apply(enterpriseDTO);
		return success ? Result.success("申请已提交") : Result.sysError("申请失败");
	}

	/**
	 * 获取我的企业入驻信息
	 */
	@GetMapping("/mine")
	@Operation(summary = "C端-获取我的企业入驻信息")
	public Result<EnterpriseVO> getMyEnterprise() {
		// TODO: 从 Token 获取 currentUserId，查询关联的企业信息
		log.info("查询当前登录用户的企业信息");
		EnterpriseVO enterprise = enterpriseService.getMyEnterprise();
		return Result.success(enterprise);
	}

	/**
	 * 修改企业信息
	 */
	@PutMapping("/mine/update")
	@Operation(summary = "C端-维护我的企业资料", description = "企业用户自主更新联系人、电话及企业简介等非核心审核字段")
	public Result<String> updateMyEnterprise(@RequestBody EnterpriseDTO enterpriseDTO) {
		log.info("企业用户[{}]自主更新资料", enterpriseDTO.getCompanyName());
		boolean success = enterpriseService.updateMyEnterprise(enterpriseDTO);
		return success ? Result.success("更新成功") : Result.sysError("更新失败");
	}

	/**
	 * 提交迁出申请
	 */
	@PutMapping("/mine/move-out/apply")
	@Operation(summary = "C端-提交迁出申请")
	public Result<String> applyMoveOut(@RequestParam String reason) {
		log.info("企业申请迁出，原因: {}", reason);
		boolean success = enterpriseService.applyMoveOut(reason);
		return success ? Result.success("迁出申请已提交，请等待管理员审核") : Result.sysError("操作失败");
	}

	// ================== B端管控接口 ==================


	/**
	 * 获取企业列表
	 */
	@GetMapping("/list")
	@Operation(summary = "获取企业列表")
	public Result<List<EnterpriseVO>> list() {
		log.info("查询全量企业列表");
		List<EnterpriseVO> enterpriseList = enterpriseService.listAll();
		return Result.success(enterpriseList);
	}

	/**
	 * 获取企业分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "B端-分页查询企业列表")
	public Result<PageResult<EnterpriseVO>> getEnterprisePage(EnterprisePageQueryDTO dto) {
		log.info("分页查询企业列表");
		PageResult<EnterpriseVO> pageResult = enterpriseService.getEnterprisePage(dto);
		return Result.success(pageResult);
	}

	/**
	 * 审核入驻申请
	 */
	@PutMapping("/audit/{id}")
	@Operation(summary = "B端-审核入驻申请")
	public Result<String> audit(
			@PathVariable Long id,
			@RequestParam Integer status,
			@RequestParam(required = false) String auditOpinion // ✅ 接收驳回理由
	) {
		log.info("开始审核 - 企业ID: {}, 结果状态: {}, 意见: {}", id, status, auditOpinion);
		boolean success = enterpriseService.audit(id, status, auditOpinion);
		return success ? Result.success("审核操作已完成") : Result.sysError("操作失败");
	}

	/**
	 * 获取企业审核历史流水
	 */
	@GetMapping("/audit/history/{id}")
	@Operation(summary = "管理端-查看审核历史记录")
	public Result<List<EnterpriseAuditVO>> getAuditHistory(@PathVariable Long id) {
		// 假设你在 service 中实现了获取流水的方法
		List<EnterpriseAuditVO> history = enterpriseService.getAuditHistory(id);
		return Result.success(history);
	}

	/**
	 * 获取企业详情
	 */
	@GetMapping("/{id}")
	@Operation(summary = "B端-获取企业详情")
	public Result<EnterpriseVO> getDetail(@PathVariable Long id) {
		EnterpriseVO enterprise = enterpriseService.getDetailById(id);
		return Result.success(enterprise);
	}

	/**
	 * 迁出办理
	 */
	@PutMapping("/audit-move-out/{id}")
	@Operation(summary = "B端-审核迁出申请")
	public Result<String> auditMoveOut(
			@PathVariable Long id,
			@RequestParam Integer status, // 3:同意迁出, 1:驳回申请(恢复正常)
			@RequestParam(required = false) String opinion
	) {
		log.info("审核迁出申请 - ID: {}, 结果: {}", id, status);
		boolean success = enterpriseService.auditMoveOut(id, status, opinion);
		return success ? Result.success("操作成功") : Result.sysError("操作失败");
	}

	/**
	 * 修改企业信息
	 */
	@PutMapping("/update")
	@Operation(summary = "修改企业信息")
	public Result<String> updateById(@RequestBody EnterpriseDTO enterpriseDTO) {
		log.info("管理员更新企业信息");
		boolean success = enterpriseService.updateEnterprise(enterpriseDTO);
		return success ? Result.success("更新成功") : Result.sysError("更新失败");
	}


	/**
	 * 删除企业 (支持单个或批量)
	 */
	@DeleteMapping("/{ids}")
	@Operation(summary = "B端-删除企业记录(支持批量)")
	public Result<String> delete(@PathVariable List<Long> ids) {
		log.info("删除企业记录 IDs: {}", ids);
		boolean success = enterpriseService.deleteByIds(ids);
		return success ? Result.success("删除成功") : Result.sysError("操作失败");
	}

	/**
	 * 获取待审核数量
	 */
	@GetMapping("/pending/count")
	@Operation(summary = "获取待审核数量")
	public Result<Integer> getPendingCount() {
		return Result.success(enterpriseService.getPendingCount());
	}
}