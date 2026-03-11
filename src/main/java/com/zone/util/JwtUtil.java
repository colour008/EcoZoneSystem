package com.zone.util;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.UnauthorizedException;
import com.zone.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	/**
	 * 生成 Token (最新写法)
	 */
	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpire()))
				.signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)))
				.compact();
	}

	/**
	 * 解析 Token 获取用户名 (最新写法 parserBuilder)
	 */
	public String getUsername(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)))
					.build()
					.parseClaimsJws(token)
					.getBody();
			return claims.getSubject();
		} catch (ExpiredJwtException e) {
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_EXPIRED.getMsg());
		} catch (MalformedJwtException | SecurityException | IllegalArgumentException e) {
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_INVALID.getMsg());
		}
	}

	/**
	 * 校验 Token 是否有效
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)))
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_EXPIRED.getMsg());
		} catch (Exception e) {
			throw new UnauthorizedException(ResponseCodeEnum.TOKEN_INVALID.getMsg());
		}
	}
}