package com.zone.service;


import com.zone.entity.sys.Menu;
import com.zone.entity.vo.MenuVO;

import java.util.List;

/**
 * @Author:     JamHoo
 * @Description: 菜单服务接口
 * @Date:    2026/3/18 20:47
 * @Version:    1.0
 */
public interface MenuService {

	/**
	 * 构建菜单树
	 *
	 * @param menuList
	 * @return
	 */
	List<MenuVO> buildMenuTree( List<Menu> menuList);

	/**
	 * 获取菜单列表
	 *
	 * @return
	 */
	List<Menu> listAll();

	/**
	 * 新增菜单
	 *
	 * @param menu
	 * @return
	 */
	boolean save(Menu menu);

	/**
	 * 修改菜单
	 *
	 * @param menu
	 * @return
	 */
	boolean updateById(Menu menu);

	/**
	 * 检查菜单下是否有子菜单
	 *
	 * @param id
	 * @return
	 */
	boolean hasChildByMenuId(Long id);

	/**
	 * 检查菜单是否被角色使用
	 *
	 * @param id
	 * @return
	 */
	boolean checkMenuExistRole(Long id);

	/**
	 * 删除菜单
	 *
	 * @param ids
	 * @return
	 */
	boolean removeById(Long ids);

	/**
	 * 根据角色ID查询菜单ID集合
	 *
	 * @param roleId
	 * @return
	 */
	List<Long> getMenuIdsByRoleId(Long roleId);

	/**
	 * 根据用户ID查询菜单树
	 */
	List<MenuVO> selectMenuTreeByUserId(Long userId);
}
