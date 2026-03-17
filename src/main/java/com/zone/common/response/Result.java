package com.zone.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

	private int code;
	private int bizCode;
	private String msg;
	private T data;

	// ==================== 成功 ====================
	public static <T> Result<T> success() {
		return new Result<>(200, 0, "操作成功", null);
	}

	public static <T> Result<T> success(T data) {
		return new Result<>(200, 0, "操作成功", data);
	}

	// ==================== 异常 ====================
	public static <T> Result<T> bizError(int bizCode, String msg) {
		return new Result<>(200, bizCode, msg, null);
	}

	public static <T> Result<T> sysError(String msg) {
		return new Result<>(500, -1, msg, null);
	}
}