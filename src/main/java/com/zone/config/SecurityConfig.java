package com.zone.config;

import com.zone.common.constant.AuthConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Author: JamHoo
 * @Description: Spring Security 配置
 * @Date: 2026/3/14 09:05
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 1. 禁用不需要的默认功能
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)

				// 2. 强制前后端分离的无状态会话管理
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 3. 权限控制
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(AuthConstant.WHITE_LIST).permitAll()
						.anyRequest().permitAll() // 允许所有请求通过 Security 层，由拦截器去验票
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// 生产环境必须使用强哈希加密
		return new BCryptPasswordEncoder();
	}
}