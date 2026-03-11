package com.zone.entity.sys;

import com.zone.entity.base.BaseEntity;
import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 用户实体类
 * @Date: 2026/3/8 15:20
 * @Version: 1.0
 */
@Data
public class User extends BaseEntity {

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
	 * 手机号
	 */
	private String phone;

	/**
	 * 状态
	 */
	private Integer status;


}
