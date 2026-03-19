package com.zone.service.impl;

import com.zone.common.exception.BusinessException;
import com.zone.entity.sys.Menu;
import com.zone.entity.vo.MenuVO;
import com.zone.mapper.MenuMapper;
import com.zone.service.MenuService;
import com.zone.util.SecurityUtils;
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
		List<MenuVO> voList = menuList.stream().map(menu -> {
			MenuVO vo = new MenuVO();
			BeanUtils.copyProperties(menu, vo);

			// 如果是外链 (isExternal = 1)，前端不需要组件，清空 component 避免加载错误
			if (menu.getIsExternal() != null && menu.getIsExternal() == 1) {
				vo.setComponent(null);
			}
			return vo;
		}).collect(Collectors.toList());

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

		// 4. 将空字符串转为 null，避免唯一索引冲突
		if (menu.getPerms() != null && menu.getPerms().trim().isEmpty()) {
			menu.setPerms(null);
		}
		// 5. 处理一下 component
		if (menu.getComponent() != null && menu.getComponent().trim().isEmpty()) {
			menu.setComponent(null);
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

	/**
	 * 根据用户角色编码查询权限
	 */
	@Override
	public List<MenuVO> selectMenuTreeByUserId(Long userId) {
		List<Menu> menus;

		// 1. 通过角色编码判断权限
		if (SecurityUtils.isAdmin()) {
			log.info("超级管理员 {} 正在加载全量动态路由", userId);
			// 超管加载所有 目录(M)和菜单(C)，且状态必须为 正常(1)
			menus = menuMapper.listAll().stream()
					.filter(m -> ("M".equals(m.getType()) || "C".equals(m.getType()))
							&& m.getStatus() == 1)
					.collect(Collectors.toList());
		} else {
			log.info("普通用户 {} 正在根据角色权限加载路由", userId);
			// 普通用户通过 XML 关联查询，SQL 中已包含 m.status = 1 过滤
			menus = menuMapper.selectMenuTreeByUserId(userId);
		}

		// 2. 构建树形结构并处理外链逻辑
		return buildMenuTree(menus);
	}
}
