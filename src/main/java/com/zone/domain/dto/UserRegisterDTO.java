package com.zone.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: 注册参数
 * @Date: 2026/3/15 17:02
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
	/**
	 * 用户名
	 */
	@NotBlank(message = "用户名不能为空")
	@Size(min = 4, max = 20, message = "用户名长度在4-20位之间")
	private String username;

	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	@Size(min = 6, message = "密码长度不能少于6位")
	private String password;

	/**
	 * 真实姓名
	 */
	@NotBlank(message = "真实姓名不能为空")
	private String realName;

	/**
	 * 手机号
	 */
	@Pattern(regexp = "^1[0-9]\\d{9}$", message = "手机号格式不正确")
	private String phone;

	/**
	 * 状态,1正常/0停用
	 */
	private Integer status = 1;
}
