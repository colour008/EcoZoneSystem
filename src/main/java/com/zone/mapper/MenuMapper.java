package com.zone.mapper;

import com.zone.entity.sys.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
	/**
	 * 根据用户ID查询所有权限标识(perms)
	 */
	List<String> selectPermsByUserId(@Param("userId") Long userId);

	/**
	 * 查询 parent_id 为该 id 的记录数
	 */
	int hasChildByMenuId(Long id);

	/**
	 * 检查菜单是否被角色使用
	 */
	int checkMenuExistRole(Long id);

	/**
	 * 获取角色已勾选的菜单ID列表
	 */
	List<Long> getMenuIdsByRoleId(Long roleId);

	/**
	 * 获取所有菜单
	 */
	List<Menu> listAll();

	/**
	 * 新增菜单
	 */
	int insert(Menu menu);

	/**
	 * 修改菜单
	 */
	int updateById(Menu menu);

	/**
	 * 删除菜单
	 */
	int deleteById(Long id);

	/**
	 * 根据用户ID查询菜单树
	 */
	List<Menu> selectMenuTreeByUserId(Long userId);
}
