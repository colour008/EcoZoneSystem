package com.zone.common.annotation;

import java.lang.annotation.*;

/**
 * 匿名访问注解 (放行鉴权)
 * * <p>功能描述：
 * 用于标记在 Controller 的类或方法上。
 * 被该注解标记的接口，将跳过 Token 强制校验逻辑，允许未登录用户（访客）直接访问。
 * * <p>处理机制：
 * 1. 配合 JwtInterceptor 或 Security 过滤器使用。
 * 2. 如果请求携带了合法的 Token，底层依然会解析并注入用户信息（SecurityUtils 可获取到 userId）；
 * 3. 如果请求未携带 Token 或 Token 已失效，不抛出异常，按匿名用户（userId = null）处理。
 *
 * <p>适用场景：
 * 门户首页列表、开放 API、登录/注册接口、公开的回调通知等。
 *
 * @author JamHoo
 * @date 2026-04-07
 * @see com.zone.interceptor.JwtInterceptor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {

	/**
	 * 是否仍需尝试解析 Token
	 * <p>扩展预留：默认 true。
	 * 若设为 false，则拦截器遇到此注解直接放行，完全不消耗 CPU 去解析 JWT，用于极致性能优化的纯静态接口。
	 *
	 * @return boolean
	 */
	boolean parseToken() default true;
}