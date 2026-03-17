package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.entity.dto.RoleDTO;
import com.zone.entity.dto.RolePageQueryDTO;
import com.zone.entity.sys.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 角色映射接口
 * @Date: 2026/3/16 21:14
 * @Version: 1.0
 */
@Mapper
public interface RoleMapper {
	/**
	 * 新增角色
	 *
	 * @param role
	 * @return
	 */
	int insert(Role role);

	/**
	 * 根据角色编码查询角色
	 *
	 * @param roleCode
	 * @return
	 */
	Role selectByRoleCode(String roleCode);

	/**
	 * 分页查询角色列表
	 *
	 * @param dto
	 * @return
	 */
	Page<Role> getRolePage(RolePageQueryDTO dto);

	/**
	 * 修改角色
	 *
	 * @param roleDTO
	 * @return
	 */
	int update(RoleDTO roleDTO);

	/**
	 * 根据ID查询角色
	 *
	 * @param id
	 * @return
	 */
	Role selectById(Long id);

	/**
	 * 查询角色编码是否已存在（排除当前ID自身）
	 * 修改时专用：判断编码是否被【其他角色】占用
	 */
	Role selectByRoleCodeExcludeId(@Param("roleCode") String roleCode, @Param("id") Long id);

	/**
	 * 批量删除角色
	 *
	 * @param ids
	 * @return
	 */
	int deleteByIds(List<Long> ids);

	/**
	 * 统计角色被用户关联的数量
	 * @param roleIds 角色ID集合
	 * @return 关联的数量
	 */
	int countUserRoleByRoleIds(@Param("roleIds") List<Long> roleIds);

	/**
	 * 根据用户ID查询角色编码
	 * @param id 用户ID
	 * @return 角色编码集合
	 */
	List<String> getRoleCodesByUserId(Long id);

	/**
	 * 查询所有有效的角色
	 * @return 角色列表
	 */
	List<Role> selectAllActiveRoles();
}
