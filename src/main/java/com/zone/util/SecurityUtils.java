package com.zone.util;

import com.zone.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @Author: JamHoo
 * @Description: 安全服务工具类
 * @Date: 2026/3/18 22:20
 * @Version: 1.0
 */

public class SecurityUtils {

	/**
	 * 获取当前登录用户ID
	 */
	public static Long getUserId() {
		return (Long) getRequest().getAttribute("userId");
	}

	/**
	 * 获取当前登录用户名
	 */
	public static String getUsername() {
		return (String) getRequest().getAttribute("username");
	}

	/**
	 * 获取请求对象
	 * 利用 Spring 提供的 RequestContextHolder 随时随地获取 Request
	 */
	private static HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			throw new UnauthorizedException("非 Web 环境无法获取用户信息");
		}
		return attributes.getRequest();
	}
}
