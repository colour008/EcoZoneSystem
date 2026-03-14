package com.zone.entity.dto;

import com.zone.entity.base.PageQuery;
import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 分页查询DTO
 * @Date: 2026/3/13 20:35
 * @Version: 1.0
 */
@Data
public class UserPageQueryDTO extends PageQuery {
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


}
