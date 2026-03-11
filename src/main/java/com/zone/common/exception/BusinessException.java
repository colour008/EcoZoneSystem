package com.zone.common.exception;

import com.zone.common.enums.ResponseCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final int bizCode;

	// 1. 传入枚举（推荐）
	public BusinessException(ResponseCodeEnum codeEnum) {
		super(codeEnum.getMsg());
		this.bizCode = codeEnum.getBizCode();
	}

	// 2. 传入枚举 + 自定义消息
	public BusinessException(ResponseCodeEnum codeEnum, String message) {
		super(message);
		this.bizCode = codeEnum.getBizCode();
	}

	// 3. 直接传字符串（修复你现在的报错！！！）
	public BusinessException(String message) {
		super(message);
		this.bizCode = ResponseCodeEnum.BUSINESS_ERROR.getBizCode();
	}
}