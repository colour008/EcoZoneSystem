package com.zone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * @Author: JamHoo
 * @Description: 跨域配置
 * @Date: 2026/3/14 09:01
 * @Version: 1.0
 */
@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		// 1. 允许前端地址（生产环境建议写具体前端域名，不要用 *）
		config.addAllowedOriginPattern("*");

		// 2. 允许跨域传递 Cookie / Token
		config.setAllowCredentials(true);

		// 3. 允许所有请求头
		config.addAllowedHeader("*");

		// 4. 允许所有请求方式 GET POST PUT DELETE OPTIONS
		config.addAllowedMethod("*");

		// 5. 暴露请求头（关键：让前端能拿到 JWT 等自定义头）
		config.addExposedHeader("Authorization");
		config.addExposedHeader("Content-Type");

		// 6. 预检请求缓存时间（企业级优化，减少 OPTIONS 请求）
		config.setMaxAge(3600L);

		// 7. 绑定拦截规则
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}