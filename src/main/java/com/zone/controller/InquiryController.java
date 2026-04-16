package com.zone.controller;

import com.zone.common.annotation.Anonymous;
import com.zone.common.response.Result;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.InquiryPageQueryDTO;
import com.zone.domain.dto.InquirySubmitDTO;
import com.zone.domain.entity.InquiryRecord;
import com.zone.domain.vo.InquiryVO;
import com.zone.mapper.InquiryRecordMapper;
import com.zone.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inquiry")
@Tag(name = "意向留言接口")
@Slf4j
public class InquiryController {

	@Autowired
	private InquiryService inquiryService;

	@Autowired
	private InquiryRecordMapper inquiryRecordMapper;

	// ================== C端：公开入口 (游客可见) ==================

	/**
	 * C端-提交意向留言
	 * @param dto
	 * @param request
	 * @return
	 */
	@Anonymous
	@PostMapping("/public/submit")
	@Operation(summary = "C端-提交意向留言", description = "自带基于内存和数据库的双重防刷机制")
	public Result<String> submitPublicInquiry(@RequestBody @Validated InquirySubmitDTO dto, HttpServletRequest request) {
		String ip = getClientIp(request);
		inquiryService.submitInquiry(dto, ip);
		return Result.success("提交成功，我们将在2个工作日内联系您！");
	}

	// ================== B端：园区管理入口 ==================

	/**
	 * 获取意向留言分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "B端-获取意向留言分页列表")
	public Result<PageResult<InquiryVO>> getAdminPage(InquiryPageQueryDTO dto) {
		return Result.success(inquiryService.getAdminPage(dto));
	}

	/**
	 * 分配跟进人员
	 */
	@PutMapping("/assign/{id}/{handlerId}")
	@Operation(summary = "B端-分配跟进人员")
	public Result<String> assignHandler(@PathVariable Long id, @PathVariable Long handlerId) {
		return inquiryService.assignHandler(id, handlerId) ? Result.success("分配成功") : Result.sysError("分配失败");
	}

	/**
	 * 填写跟进记录
	 */
	@PutMapping("/record")
	@Operation(summary = "B端-填写跟进记录", description = "参数需传入 id, result(内容), status(状态:1跟进中,3已完结,4无效)")
	public Result<String> recordFollowUp(Long id, String result, Integer status) {
		return inquiryService.recordFollowUp(id, result, status) ? Result.success("记录成功") : Result.sysError("记录失败");
	}

	/**
	 * 一键转入驻
	 */
	@PostMapping("/convert/{id}")
	@Operation(summary = "B端-一键转入驻", description = "将留言转化为正式的企业账号及档案")
	public Result<String> convertToEnterprise(@PathVariable Long id) {
		return inquiryService.convertToEnterprise(id) ? Result.success("转化成功，已生成企业账号") : Result.sysError("转化失败");
	}

	/**
	 * 删除无效留言
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "B端-删除无效留言")
	public Result<String> delete(@PathVariable Long id) {
		return inquiryService.deleteInquiry(id) ? Result.success("删除成功") : Result.sysError("删除失败");
	}

	/**
	 * 获取指定留言的所有跟进记录
	 */
	@GetMapping("/records/{inquiryId}")
	@Operation(summary = "B端-获取指定留言的所有跟进记录")
	public Result<List<InquiryRecord>> getFollowRecords(@PathVariable Long inquiryId) {
		return Result.success(inquiryRecordMapper.selectByInquiryId(inquiryId));
	}

	// ================== 辅助方法 ==================

	/**
	 * 获取真实的客户端IP
	 */
	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return StringUtils.hasText(ip) ? ip.split(",")[0] : "127.0.0.1";
	}
}