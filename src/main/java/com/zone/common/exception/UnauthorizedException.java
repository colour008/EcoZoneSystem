package com.zone.common.exception;

/**
 * 未授权异常（未登录、Token过期、Token无效）
 */
public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException() {
		super("登录已过期，请重新登录");
	}

	public UnauthorizedException(String message) {
		super(message);
	}
}