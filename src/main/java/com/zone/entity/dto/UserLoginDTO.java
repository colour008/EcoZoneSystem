package com.zone.entity.dto;

import com.zone.entity.base.BaseEntity;
import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 用户登录参数
 * @Date: 2026/3/8 15:23
 * @Version: 1.0
 */
@Data
public class UserLoginDTO extends BaseEntity {
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
}
