package com.zone.entity.dto;

import com.zone.entity.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: JamHoo
 * @Description: 角色查询参数
 * @Date: 2026/3/16 21:28
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageQueryDTO extends PageQuery {
	/**
	 * 角色名
	 */
	private String roleName;

	/**
	 * 角色编码
	 */
	private String roleCode;
}
