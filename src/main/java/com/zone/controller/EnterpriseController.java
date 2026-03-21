package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.EnterpriseDTO;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.vo.EnterpriseVO;
import com.zone.service.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 企业管理控制器
 * @Date: 2026/3/21 14:27
 * @Version: 1.0
 */
@RestController
@Slf4j
@RequestMapping("/enterprise")
@Tag(name = "企业管理接口", description = "实现企业入驻及信息管理")
public class EnterpriseController {
	@Autowired
	private EnterpriseService enterpriseService;

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
	 * 分页查询企业列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询企业列表")
	public Result<PageResult<EnterpriseVO>> getEnterprisePage(EnterprisePageQueryDTO dto) {
		log.info("分页查询企业列表");
		PageResult<EnterpriseVO> pageResult = enterpriseService.getEnterprisePage(dto);
		return Result.success(pageResult);
	}


	/**
	 * 提交入驻申请
	 */
	@PostMapping("/apply")
	@Operation(summary = "提交入驻申请")
	public Result<String> apply(@RequestBody EnterpriseDTO enterpriseDTO) {
		log.info("提交入驻申请: {}", enterpriseDTO);
		boolean success = enterpriseService.apply(enterpriseDTO);
		return success ? Result.success("申请已提交，请耐心等待园区审核") : Result.sysError("申请失败");
	}

	/**
	 * 审核企业入驻
	 * @param id 企业ID
	 * @param status 1:通过, 2:驳回
	 */
	@PutMapping("/audit/{id}")
	@Operation(summary = "审核入驻申请")
	public Result<String> audit(@PathVariable Long id, @RequestParam Integer status) {
		log.info("审核入驻申请: {}", id);
		boolean success = enterpriseService.audit(id, status);
		String msg = status == 1 ? "审核通过，企业已正式入驻" : "申请已驳回";
		return success ? Result.success(msg) : Result.sysError("审核操作失败");
	}
}
