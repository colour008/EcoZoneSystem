package com.zone.config;

import com.zone.common.constant.AuthConstant;
import com.zone.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

/**
 * Web 综合配置类
 * 职责：拦截器注册 + 跨域全局配置
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final JwtInterceptor jwtInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册 JWT 业务拦截器
		registry.addInterceptor(jwtInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(AuthConstant.WHITE_LIST);
	}

	/**
	 * 全局跨域过滤器
	 * 设置最高优先级，确保在 Security 和 Interceptor 之前处理 OPTIONS 预检请求
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// 1. 允许携带凭证（Cookie/Token）
		config.setAllowCredentials(true);

		// 2. 允许的源模式（使用 Pattern 比 addAllowedOrigin "*" 更灵活且符合安全规范）
		config.setAllowedOriginPatterns(Collections.singletonList("*"));

		// 3. 允许的请求头
		config.addAllowedHeader("*");

		// 4. 允许的 HTTP 方法
		config.addAllowedMethod("*");

		// 5. 关键：暴露响应头，确保前端 JS 能读取到自定义的 Authorization 等信息
		config.addExposedHeader("Authorization");
		config.addExposedHeader("Content-Type");

		// 6. 预检请求缓存时间（1 小时），减少浏览器频繁发送 OPTIONS 请求
		config.setMaxAge(3600L);

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}