package com.zone.common.exception;

/**
 * 权限不足异常
 */
public class PermissionDeniedException extends RuntimeException {

	public PermissionDeniedException() {
		super("权限不足，无法访问");
	}

	public PermissionDeniedException(String message) {
		super(message);
	}
}