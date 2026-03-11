package com.zone.interceptor;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.UnauthorizedException;
import com.zone.config.JwtConfig;
import com.zone.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

	private final JwtConfig jwtConfig;
	private final JwtUtil jwtUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
		// 1. 获取请求头
		String header = request.getHeader(jwtConfig.getHeader());

		// 2. 无 token
		if (header == null || !header.startsWith(jwtConfig.getTokenPrefix())) {
			throw new UnauthorizedException(ResponseCodeEnum.USER_NOT_LOGIN.getMsg());
		}

		// 3. 截取 token
		String token = header.replaceFirst(jwtConfig.getTokenPrefix() + " ", "");

		// 4. 校验并获取用户名
		String username = jwtUtil.getUsername(token);

		// 5. 存入 request，给 controller 使用
		request.setAttribute("username", username);

		return true;
	}
}