package com.zone.config;

import com.zone.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final JwtInterceptor jwtInterceptor;

	/**
	 * 放行白名单
	 */
	private static final String[] WHITE_LIST = {
			"/auth/login",
			"/auth/register",
			"/common/**",
			"/doc.html",
			"/swagger-ui/**",
			"/v3/api-docs/**"
	};

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(WHITE_LIST);
	}

	/**
	 * 跨域配置
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowCredentials(true)
				.allowedOriginPatterns("*")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
				.allowedHeaders("*")
				.maxAge(3600);
	}
}