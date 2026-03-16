package com.zone.interceptor;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.UnauthorizedException;
import com.zone.config.JwtConfig;
import com.zone.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 企业级 JWT 拦截器
 * 优化点：
 * 1. 增加 OPTIONS 请求放行，确保跨域无缝衔接
 * 2. 健壮的 Token 截取逻辑
 * 3. 减少重复解析，提升单次请求性能
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

		// 2. 获取并校验请求头
		String header = request.getHeader(jwtConfig.getHeader());
		String prefix = jwtConfig.getTokenPrefix();

		if (!StringUtils.hasText(header) || !header.startsWith(prefix)) {
			log.warn("请求拒绝：未检测到有效的 Authorization 请求头, Path: {}", request.getRequestURI());
			throw new UnauthorizedException(ResponseCodeEnum.USER_NOT_LOGIN.getMsg());
		}

		// 3. 健壮地截取 Token 字符串 (去掉前缀并修剪空格)
		// 比如 "Bearer xxxxx" -> "xxxxx"
		String token = header.substring(prefix.length()).trim();

		if (!StringUtils.hasText(token)) {
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_INVALID.getMsg());
		}

		// 4. 解析 Token (这里只需解析一次，因为 JwtUtil 内部已经封装了过期和校验逻辑)
		// 建议在 JwtUtil 中增加一个返回所有 Claims 的方法，或者像下面这样按需获取
		String username = jwtUtil.getUsername(token);
		Long userId = jwtUtil.getUserId(token);

		// 5. 存入 request 域，方便后续 Controller 或 Service 使用
		request.setAttribute("username", username);
		request.setAttribute("userId", userId);

		log.debug("用户验证通过: [ID: {}, Name: {}]", userId, username);
		return true;
	}
}