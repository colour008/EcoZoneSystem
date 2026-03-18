package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.RoleDTO;
import com.zone.entity.dto.RolePageQueryDTO;
import com.zone.entity.sys.Role;
import com.zone.entity.vo.RoleVO;
import com.zone.service.MenuService;
import com.zone.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 角色控制器
 * @Date: 2026/3/17 09:21
 * @Version: 1.0
 */
@RestController
@RequestMapping("/role")
@Slf4j
@Tag(name = "角色管理接口", description = "实现角色CRUD")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@Autowired
	private MenuService menuService;

	/**
	 * 获取所有角色列表(不分页)
	 *
	 * @return
	 */
	@GetMapping("/listAll")
	@Operation(summary = "获取所有角色列表(不分页)")
	public Result<List<Role>> listAll() {
		// 调用 Service 获取所有可用角色
		List<Role> list = roleService.listAll();
		return Result.success(list);
	}

	/**
	 * 新增角色
	 *
	 * @param roleDTO
	 * @return
	 */
	@PostMapping("/add")
	@Operation(summary = "新增角色", description = "新增角色")
	public Result<String> addRole(@RequestBody RoleDTO roleDTO) {
		log.info("新增角色");
		boolean flag = roleService.addRole(roleDTO);
		if (flag) {
			return Result.success("新增角色成功");
		}
		return Result.sysError("新增角色失败");
	}

	/**
	 * 分页查询角色列表
	 *
	 * @param dto
	 * @return pageResult
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询角色列表")
	public Result<PageResult<RoleVO>> getRolePage(RolePageQueryDTO dto) {
		log.info("分页查询角色列表");
		PageResult<RoleVO> pageResult = roleService.getRolePage(dto);
		return Result.success(pageResult);
	}

	/**
	 * 修改角色
	 *
	 * @param id
	 * @param roleDTO
	 * @return
	 */
	@PutMapping("/{id}")
	@Operation(summary = "修改角色", description = "修改角色")
	public Result<?> update(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
		log.info("修改角色");
		if (!id.equals(roleDTO.getId())) {
			return Result.bizError(400, "路径ID与请求体ID不一致");
		}
		boolean success = roleService.updateById(roleDTO);
		return success ? Result.success() : Result.sysError("更新失败");
	}

	/**
	 * 删除角色
	 *
	 * @param ids 支持单一删除和批量删除
	 * @return
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "删除角色", description = "删除角色")
	public Result<String> delete(@RequestBody List<Long> ids) {
		boolean success = roleService.deleteByIds(ids);
		return success ? Result.success("删除成功") : Result.sysError("删除失败或数据不存在");
	}


	/**
	 * 获取角色已分配的菜单ID列表
	 */
	@GetMapping("/{roleId}/menus")
	@Operation(summary = "获取角色拥有的菜单ID集合")
	public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
		log.info("查询角色权限回显，角色ID: {}", roleId);
		// 直接调用之前 MenuService 实现好的逻辑
		List<Long> menuIds = menuService.getMenuIdsByRoleId(roleId);
		return Result.success(menuIds);
	}

	/**
	 * 保存角色权限
	 */
	@PostMapping("/{roleId}/menus")
	@Operation(summary = "保存角色分配的权限")
	public Result<String> saveRoleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
		log.info("分配角色权限，角色ID: {}, 菜单数量: {}", roleId, menuIds.size());
		roleService.updateRoleMenus(roleId, menuIds);
		return Result.success("权限分配成功");
	}
}
