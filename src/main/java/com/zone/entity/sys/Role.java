package com.zone.entity.sys;

import com.zone.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Author: JamHoo
 * @Description: 角色实体类
 * @Date: 2026/3/16 21:13
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Role extends BaseEntity {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 角色名
	 */
	private String roleName;

	/**
	 * 角色编码
	 */
	private String roleCode;
}
