package com.zone.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口返回格式（增强版，含业务状态码）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
	/**
	 * 通用状态码：200成功，500系统异常，401未授权，403权限不足
	 */
	private int code;
	/**
	 * 业务状态码：如4001-号源不足，4002-余额不足
	 */
	private int bizCode;
	/**
	 * 响应消息
	 */
	private String msg;
	/**
	 * 响应数据
	 */
	private T data;

	// 成功响应（无数据）
	public static <T> Result<T> success() {
		return new Result<>(200, 0, "操作成功", null);
	}

	// 成功响应（有数据）
	public static <T> Result<T> success(T data) {
		return new Result<>(200, 0, "操作成功", data);
	}

	// 业务异常响应
	public static <T> Result<T> bizError(int bizCode, String msg) {
		return new Result<>(200, bizCode, msg, null);
	}

	// 系统异常响应
	public static <T> Result<T> sysError(String msg) {
		return new Result<>(500, -1, msg, null);
	}

	// 自定义响应
	public static <T> Result<T> result(int code, int bizCode, String msg, T data) {
		Result<T> result = new Result<>();
		result.setCode(code);
		result.setBizCode(bizCode);
		result.setMsg(msg);
		result.setData(data);
		return result;
	}
}
