package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.domain.entity.Menu;
import com.zone.domain.vo.MenuVO;
import com.zone.service.MenuService;
import com.zone.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 菜单权限控制器
 * @Date: 2026/3/18 20:47
 * @Version: 1.0
 */
@RestController
@RequestMapping("/menu")
@Slf4j
@Tag(name = "菜单管理接口", description = "实现菜单权限的树形CRUD")
public class MenuController {
	@Autowired
	private MenuService menuService;

	/**
	 * 获取菜单树列表
	 */
	@GetMapping("/list")
	@Operation(summary = "获取菜单树列表", description = "返回全量菜单的树形结构")
	public Result<List<MenuVO>> list() {
		log.info("查询全量菜单树");
		// 先查出扁平List，再通过service递归成树
		List<Menu> allMenus = menuService.listAll();
		List<MenuVO> tree = menuService.buildMenuTree(allMenus);
		return Result.success(tree);
	}

	/**
	 * 新增菜单
	 */
	@PostMapping("/add")
	@Operation(summary = "新增菜单")
	public Result<String> add(@RequestBody Menu menu) {
		log.info("新增菜单: {}", menu.getMenuName());
		boolean success = menuService.save(menu);
		return success ? Result.success("新增成功") : Result.sysError("新增失败");
	}

	/**
	 * 修改菜单
	 */
	@PutMapping("/{id}")
	@Operation(summary = "修改菜单")
	public Result<String> update(@PathVariable Long id, @RequestBody Menu menu) {
		log.info("修改菜单ID: {}", id);
		if (!id.equals(menu.getId())) {
			return Result.bizError(400, "请求ID不一致");
		}
		boolean success = menuService.updateById(menu);
		return success ? Result.success("修改成功") : Result.sysError("修改失败");
	}

	/**
	 * 删除菜单
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "删除菜单", description = "注意：若有子菜单则不允许删除")
	public Result<String> delete(@PathVariable Long id) {
		log.info("删除菜单ID: {}", id);
		// 1. 检查是否有子菜单
		if (menuService.hasChildByMenuId(id)) {
			return Result.bizError(400, "存在子菜单，不允许删除");
		}
		// 2. 检查是否分配给了角色 (逻辑类似于之前的角色删除校验)
		if (menuService.checkMenuExistRole(id)) {
			return Result.bizError(400, "菜单已分配给角色，无法删除");
		}

		boolean success = menuService.removeById(id);
		return success ? Result.success("删除成功") : Result.sysError("删除失败");
	}

	/**
	 * 获取角色对应的菜单权限树（用于分配权限时的回显）
	 */
	@GetMapping("/roleMenuTreeSelect/{roleId}")
	@Operation(summary = "根据角色ID获取菜单权限树")
	public Result<List<Long>> getRoleMenuTreeSelect(@PathVariable Long roleId) {
		List<Long> checkedKeys = menuService.getMenuIdsByRoleId(roleId);
		return Result.success(checkedKeys);
	}

	/**
	 * 获取路由信息
	 */
	@GetMapping("/getRouters")
	@Operation(summary = "获取路由信息", description = "获取当前登录用户的动态路由菜单树")
	public Result<List<MenuVO>> getRouters() {
		// 获取登录用户ID
		Long userId = SecurityUtils.getUserId();
		String userName = SecurityUtils.getUsername();
		log.info("用户 {} 正在加载路由", userName);
		List<MenuVO> menuTree = menuService.selectMenuTreeByUserId(userId);
		return Result.success(menuTree);
	}
}
