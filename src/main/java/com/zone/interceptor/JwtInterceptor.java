package com.zone.interceptor;

import com.zone.common.annotation.Anonymous;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.UnauthorizedException;
import com.zone.config.JwtConfig;
import com.zone.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 企业级 JWT 拦截器
 * 优化点：
 * 1. 增加 OPTIONS 请求放行，确保跨域无缝衔接
 * 2. 健壮的 Token 截取逻辑
 * 3. 减少重复解析，提升单次请求性能
 * 4. 支持可选登录（匿名访问也能解析 Token）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

	private final JwtConfig jwtConfig;
	private final JwtUtil jwtUtil;

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request,
	                         @NonNull HttpServletResponse response,
	                         @NonNull Object handler) {

		// 1. 放行 OPTIONS 预检请求（跨域必备双保险）
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return true;
		}

		// 2. 获取并尝试校验 Token
		String header = request.getHeader(jwtConfig.getHeader());
		String prefix = jwtConfig.getTokenPrefix();

		// 标识是否成功获取到用户信息
		boolean hasUser = false;

		if (StringUtils.hasText(header) && header.startsWith(prefix)) {
			try {
				// 3. 健壮地截取 Token 字符串
				String token = header.substring(prefix.length()).trim();
				if (StringUtils.hasText(token)) {
					// 4. 解析 JWT
					Claims claims = jwtUtil.parseToken(token);

					String username = claims.getSubject();
					Long userId = claims.get("userId", Long.class);

					@SuppressWarnings("unchecked")
					List<String> roleCodes = claims.get("roles", List.class);

					// 5. 存入 request 域，供后续 SecurityUtils 使用
					request.setAttribute("username", username);
					request.setAttribute("userId", userId);
					request.setAttribute("roles", roleCodes);

					log.debug("用户验证通过: [ID: {}, Name: {}]", userId, username);
					hasUser = true;
				}
			} catch (Exception e) {
				// Token 解析失败（过期、伪造等），记录日志但不在此处抛出异常
				// 因为可能该接口是允许匿名访问的
				log.debug("Token 解析失败，可能是匿名访问或 Token 已失效: {}", e.getMessage());
			}
		}

		// 6. 权限判定逻辑
		if (!hasUser) {
			// 如果没有用户信息，检查当前接口是否允许匿名访问
			if (handler instanceof HandlerMethod) {
				HandlerMethod hm = (HandlerMethod) handler;
				// 检查方法或类上是否有 @Anonymous 注解
				boolean isAnonymous = hm.hasMethodAnnotation(Anonymous.class)
						|| hm.getBeanType().isAnnotationPresent(Anonymous.class);

				if (isAnonymous) {
					log.debug("匿名访问放行, Path: {}", request.getRequestURI());
					return true; // 允许放行，SecurityUtils 获取到的将是 null
				}
			}

			// 既没有有效 Token，也不是匿名接口，则抛出异常
			log.warn("请求拒绝：未检测到有效的身份信息且该接口非匿名, Path: {}", request.getRequestURI());
			throw new UnauthorizedException(ResponseCodeEnum.USER_NOT_LOGIN.getMsg());
		}

		return true;
	}
}