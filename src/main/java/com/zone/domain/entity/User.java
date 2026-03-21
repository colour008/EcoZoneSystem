package com.zone.domain.entity;

import com.zone.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Author: JamHoo
 * @Description: 用户实体类
 * @Date: 2026/3/8 15:20
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class User extends BaseEntity {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 姓名
	 */
	private String realName;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 角色名称
	 */
	private String roleName;

}
