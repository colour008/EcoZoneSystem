package com.zone.common.exception;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 业务异常（最常用）
	 */
	@ExceptionHandler(BusinessException.class)
	public Result<?> handleBusinessException(BusinessException e) {
		log.error("业务异常：{}", e.getMessage());
		// HTTP状态码固定200，返回业务异常码
		return Result.bizError(e.getBizCode(), e.getMessage());
	}

	/**
	 * 系统异常（未知错误）
	 */
	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e) {
		log.error("系统异常：", e);
		return Result.sysError("服务器繁忙，请稍后重试");
	}

	/**
	 * 未授权/登录过期（可选）
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public Result<?> handleUnauthorized() {
		return Result.result(401, ResponseCodeEnum.TOKEN_EXPIRED.getBizCode(),
				ResponseCodeEnum.TOKEN_EXPIRED.getMsg(), null);
	}

	/**
	 * 权限不足（可选）
	 */
	@ExceptionHandler(PermissionDeniedException.class)
	public Result<?> handlePermissionDenied() {
		return Result.result(403, ResponseCodeEnum.PERMISSION_DENIED.getBizCode(),
				ResponseCodeEnum.PERMISSION_DENIED.getMsg(), null);
	}
}