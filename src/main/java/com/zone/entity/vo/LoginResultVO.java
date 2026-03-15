package com.zone.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}


