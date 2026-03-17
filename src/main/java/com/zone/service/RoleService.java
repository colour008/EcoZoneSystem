package com.zone.service;

import com.zone.entity.base.PageResult;
import com.zone.entity.dto.RoleDTO;
import com.zone.entity.dto.RolePageQueryDTO;

/**
 * @Author: JamHoo
 * @Description: 角色服务接口
 * @Date: 2026/3/16 21:26
 * @Version: 1.0
 */

public interface RoleService {

	/**
	 * 新增角色
	 * @param roleDTO
	 * @return
	 */
	boolean addRole(RoleDTO roleDTO);

	/**
	 * 分页查询角色列表
	 * @param dto
	 * @return
	 */
	PageResult getRolePage(RolePageQueryDTO dto);

	/**
	 * 修改角色
	 * @param roleDTO
	 * @return
	 */
	boolean updateById(RoleDTO roleDTO);
}
