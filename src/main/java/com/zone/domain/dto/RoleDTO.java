package com.zone.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: JamHoo
 * @Description: 角色参数
 * @Date: 2026/3/16 21:26
 * @Version: 1.0
 */
@Data
public class RoleDTO implements Serializable {
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 角色名
	 */
	@NotBlank(message = "角色名不能为空")
	private String roleName;

	/**
	 * 角色排序
	 */
	private Integer roleSort;

	/**
	 * 角色编码
	 */
	@NotBlank(message = "角色编码不能为空")
	private String roleCode;
}
