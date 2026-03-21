package com.zone.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 返回登录结果
 * @Date: 2026/3/15 10:44
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResultVO {
	private String token;
	private UserVO user; // 只返回必要的字段，不要直接把包含密码的 User 实体丢出去
	private List<String> roles;       // 角色编码列表，如 ["ROLE_ADMIN", "ROLE_ENTERPRISE"]
	private List<String> permissions; // 权限标识列表，如 ["entity:user:add", "entity:user:delete"]
}


