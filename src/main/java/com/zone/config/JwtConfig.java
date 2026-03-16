package com.zone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: JamHoo
 * @Description: JWT 配置
 * @Date: 2026/3/14 09:05
 * @Version: 1.0
 */
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

	/**
	 * 密钥
	 */
	private String secret;

	/**
	 * 过期时间
	 */
	private long expire;

	/**
	 * 请求头
	 */
	private String header;

	/**
	 * 令牌前缀
	 */
	private String tokenPrefix;
}