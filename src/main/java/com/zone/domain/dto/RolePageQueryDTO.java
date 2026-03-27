package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: JamHoo
 * @Description: 角色分页查询参数
 * @Date: 2026/3/16 21:28
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageQueryDTO extends PageQuery implements Serializable {
	/**
	 * 角色名
	 */
	private String roleName;

	/**
	 * 角色编码
	 */
	private String roleCode;
}
