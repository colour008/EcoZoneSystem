package com.zone.util;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.UnauthorizedException;
import com.zone.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 优化点：
 * 1. 预计算密钥，减少重复转换开销
 * 2. 统一解析入口，封装复杂的异常捕获
 * 3. 规范化异常抛出，确保过期和无效 Token 的语义清晰
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;
	private SecretKey secretKey; // 预存密钥对象

	/**
	 * Bean初始化时预计算密钥，避免每次生成/解析时重复调用 getBytes 和 hmacShaKeyFor
	 */
	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 生成 Token
	 * @param userId   用户ID (存入自定义 claim)
	 * @param username 用户名 (存入 subject)
	 * @return 签名的 JWT 字符串
	 */
	public String generateToken(Long userId, String username) {
		return Jwts.builder()
				.setSubject(username)
				.claim("userId", userId)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpire()))
				.signWith(secretKey) // 直接使用预存密钥
				.compact();
	}

	/**
	 * 核心：统一解析 Token 的 Claims
	 * 将所有解析异常在这里统一处理，避免分散在各个方法中
	 * @param token JWT字符串
	 * @return 载荷信息
	 */
	private Claims parseAllClaims(String token) {
		try {
			return Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (ExpiredJwtException e) {
			log.warn("JWT令牌已过期: {}", e.getMessage());
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_EXPIRED.getMsg());
		} catch (SecurityException | MalformedJwtException e) {
			log.error("JWT签名无效或格式错误: {}", e.getMessage());
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_INVALID.getMsg());
		} catch (Exception e) {
			log.error("JWT解析异常: {}", e.getMessage());
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_INVALID.getMsg());
		}
	}

	/**
	 * 从 Token 中获取用户ID
	 */
	public Long getUserId(String token) {
		return parseAllClaims(token).get("userId", Long.class);
	}

	/**
	 * 从 Token 中获取用户名 (Subject)
	 */
	public String getUsername(String token) {
		return parseAllClaims(token).getSubject();
	}

	/**
	 * 校验 Token 是否有效
	 * 调用此方法若 Token 无效或过期，会直接在 parseAllClaims 中抛出相应的 UnauthorizedException
	 * @param token JWT字符串
	 * @return 是否校验通过
	 */
	public boolean validateToken(String token) {
		return parseAllClaims(token) != null;
	}

	/**
	 * 获取 Token 的剩余有效时间 (可选，用于做缓存或续期判断)
	 * @param token JWT字符串
	 * @return 剩余毫秒数
	 */
	public long getRemainingTime(String token) {
		Date expiration = parseAllClaims(token).getExpiration();
		return expiration.getTime() - System.currentTimeMillis();
	}
}