package com.zone.service;

import com.zone.entity.base.PageResult;
import com.zone.entity.dto.RoleDTO;
import com.zone.entity.dto.RolePageQueryDTO;
import com.zone.entity.sys.Role;

import java.util.List;

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

	/**
	 * 删除角色
	 * @param ids
	 * @return
	 */
	boolean deleteByIds(List<Long> ids);

	/**
	 * 获取所有可用角色
	 */
	List<Role> listAll();

	/**
	 * 更新角色分配的菜单权限
	 */
	void updateRoleMenus(Long roleId, List<Long> menuIds);
}
