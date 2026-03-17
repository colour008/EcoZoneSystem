package com.zone.entity.vo;

import com.zone.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: 角色VO
 * @Date: 2026/3/17 10:46
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO extends BaseEntity {
	/**
	 * 角色名
	 */
	private String roleName;
	/**
	 * 角色编码
	 */
	private String roleCode;
}
