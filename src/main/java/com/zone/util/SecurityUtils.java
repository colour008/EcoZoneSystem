package com.zone.util;

import com.zone.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 安全服务工具类
 * @Date: 2026/3/18 22:20
 * @Version: 1.0
 */
public class SecurityUtils {

	/**
	 * 超级管理员角色编码常量
	 * 必须与数据库 sys_role 表中的 role_code 字段精确对应
	 */
	public static final String ADMIN_ROLE_CODE = "ROLE_ADMIN";

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
	 * 获取当前登录用户的角色编码列表
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getRoleCodes() {
		Object roles = getRequest().getAttribute("roles");
		if (roles instanceof List) {
			return (List<String>) roles;
		}
		return Collections.emptyList();
	}

	/**
	 * 判断是否为超级管理员
	 * 逻辑：检查当前用户的角色列表中是否包含 ADMIN_ROLE_CODE (ROLE_ADMIN)
	 */
	public static boolean isAdmin() {
		List<String> roles = getRoleCodes();
		// 匹配数据库中的 ROLE_ADMIN
		return roles != null && roles.contains(ADMIN_ROLE_CODE);
	}

	/**
	 * 获取请求对象
	 * 利用 Spring 提供的 RequestContextHolder 获取当前线程绑定的 Request
	 */
	private static HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			throw new UnauthorizedException("非 Web 环境无法获取用户信息");
		}
		return attributes.getRequest();
	}
}