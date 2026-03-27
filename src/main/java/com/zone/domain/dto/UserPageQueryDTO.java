package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: JamHoo
 * @Description: 用户查询参数
 * @Date: 2026/3/13 20:35
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageQueryDTO extends PageQuery implements Serializable {
	/**
	 * 用户名
	 */
	private String username;


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
