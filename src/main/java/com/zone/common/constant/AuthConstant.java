package com.zone.common.constant;


/**
 * @Author: JamHoo
 * @Description: 权限校验常量
 * @Date: 2026/3/14 09:05
 * @Version: 1.0
 */
public interface AuthConstant {
	/** 放行白名单 */
	String[] WHITE_LIST = {
			"/auth/login",
			"/auth/register",
			"/common/**",
			"/doc.html",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/favicon.ico"
	};
}