package com.zone.service.impl;

import com.zone.common.exception.BusinessException;
import com.zone.entity.sys.Menu;
import com.zone.entity.vo.MenuVO;
import com.zone.mapper.MenuMapper;
import com.zone.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: JamHoo
 * @Description: 菜单服务实现类
 * @Date: 2026/3/18 20:41
 * @Version: 1.0
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuMapper menuMapper;

	/**
	 * 构建菜单树
	 */
	@Override
	public List<MenuVO> buildMenuTree(List<Menu> menuList) {
		// 1. 先把 POJO 转成 VO 列表
		List<MenuVO> voList = menuList.stream().map(menu -> {
			MenuVO vo = new MenuVO();
			BeanUtils.copyProperties(menu, vo);
			return vo;
		}).collect(Collectors.toList());

		// 2. 核心递归：找出所有顶级节点（parentId 为 0 的）
		return voList.stream()
				.filter(menu -> menu.getParentId() == 0)
				.map(menu -> {
					menu.setChildren(getChildren(menu, voList));
					return menu;
				})
				.sorted(Comparator.comparingInt(m -> (m.getOrderNum() == null ? 0 : m.getOrderNum())))
				.collect(Collectors.toList());
	}

	/**
	 * 获取所有菜单
	 */
	@Override
	public List<Menu> listAll() {
		return menuMapper.listAll();
	}

	/**
	 * 新增菜单
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean save(Menu menu) {
		// 1. 逻辑处理：如果没有设置父ID，默认设为顶级目录(0)
		if (menu.getParentId() == null) {
			menu.setParentId(0L);
		}
		// 2. 默认值：如果没传状态，默认设为正常(1)
		if (menu.getStatus() == null) {
			menu.setStatus(1);
		}
		// 3. 默认值：如果没传外链，默认设为否(0)
		if (menu.getIsExternal() == null) {
			menu.setIsExternal(0);
		}

		log.info("开始插入菜单数据: {}", menu.getMenuName());
		return menuMapper.insert(menu) > 0;
	}

	/**
	 * 修改菜单
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(Menu menu) {
		// 在修改前可以做个简单校验：不能把父菜单指向自己
		if (menu.getId() != null && menu.getId().equals(menu.getParentId())) {
			throw new BusinessException("上级菜单不能选择当前菜单本身");
		}

		log.info("开始更新菜单数据，ID: {}", menu.getId());
		return menuMapper.updateById(menu) > 0;
	}

	/**
	 * 检查菜单下是否有子菜单
	 */
	@Override
	public boolean hasChildByMenuId(Long id) {
		// 查询 parent_id 为该 id 的记录数
		return menuMapper.hasChildByMenuId(id) > 0;
	}

	/**
	 * 检查菜单下是否有角色关联
	 */
	@Override
	public boolean checkMenuExistRole(Long id) {
		// 在 sys_role_menu 中查询该 menu_id 是否被占用
		return menuMapper.checkMenuExistRole(id) > 0;
	}

	/**
	 * 删除菜单
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeById(Long id) {
		log.info("执行物理删除菜单，ID: {}", id);
		// 注意：Controller 已经通过 hasChildByMenuId 和 checkMenuExistRole 做了前置校验
		// 这里直接执行删除
		return menuMapper.deleteById(id) > 0;
	}

	/**
	 * 根据角色ID查询菜单ID集合
	 */
	@Override
	public List<Long> getMenuIdsByRoleId(Long roleId) {
		// 获取角色已勾选的菜单ID列表，用于前端 Tree 回显
		return menuMapper.getMenuIdsByRoleId(roleId);
	}

	/**
	 * 递归获取子菜单
	 */
	private List<MenuVO> getChildren(MenuVO root, List<MenuVO> all) {
		return all.stream()
				.filter(menu -> menu.getParentId().equals(root.getId()))
				.map(menu -> {
					menu.setChildren(getChildren(menu, all));
					return menu;
				})
				.sorted(Comparator.comparingInt(m -> (m.getOrderNum() == null ? 0 : m.getOrderNum())))
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuVO> selectMenuTreeByUserId(Long userId) {
		List<Menu> menus;

		// 假设 ID 为 1 的是超级管理员（或者你可以通过角色编码判断）
		if (userId == 1L) {
			// 超级管理员：查询所有类型为目录(M)和菜单(C)且状态正常的记录
			menus = menuMapper.listAll().stream()
					.filter(m -> ("M".equals(m.getType()) || "C".equals(m.getType()))
							&& m.getStatus() == 1)
					.collect(Collectors.toList());
		} else {
			// 普通用户：联表查询该用户拥有的权限
			menus = menuMapper.selectMenuTreeByUserId(userId);
		}

		// 调用你之前写好的 buildMenuTree 进行递归封装
		return buildMenuTree(menus);
	}
}
