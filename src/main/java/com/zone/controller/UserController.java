package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户控制器
 * @Date: 2026/3/13 18:34
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理接口", description = "实现用户CRUD")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 获取用户列表
	 * @return List<User>
	 */
	@GetMapping("/list")
	@Operation(summary = "获取用户列表", description = "获取用户列表")
	public Result<List<User>> getUserlist() {
		log.info("获取用户列表");
		List<User> userlist = userService.getUserlist();
		if (userlist != null){
			return Result.success(userlist);
		}
		return Result.sysError("获取用户列表失败");
	}

	/**
	 * 分页查询用户列表
	 * @param dto
	 * @return PageResult
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询用户列表", description = "分页查询用户列表")
	public Result<PageResult> getUserPage(UserPageQueryDTO dto) {
		log.info("分页查询用户列表");
		PageResult pageResult = userService.getUserPage(dto);
		if (pageResult != null){
			return Result.success(pageResult);
		}
		return Result.sysError("分页查询用户列表失败");
	}

	/**
	 * 添加用户
	 * @param user
	 * @return boolean
	 */
	@PostMapping("/add")
	@Operation(summary = "添加用户", description = "添加用户")
	public Result<String> addUser(@RequestBody User user) {
		log.info("添加用户");
		boolean flag = userService.addUser(user);
		if (flag){
			return Result.success("添加用户成功");
		}
		return Result.sysError("添加用户失败");
	}

	/**
	 * 删除用户
	 * @param ids 支持单一删除和批量删除
	 * @return boolean
	 */
	@DeleteMapping("/delete")
	public Result<String> delete(@RequestBody List<Long> ids) {
		boolean success = userService.deleteByIds(ids);
		if (success) {
			return Result.success("删除成功");
		}
		return Result.sysError("删除失败或数据不存在");
	}

	/**
	 * 修改用户
	 * @param id
	 * @param user
	 * @return boolean
	 */
	@PutMapping("/{id}")
	public Result<String> update(@PathVariable Long id, @RequestBody User user) {
		// 强制设置对象 ID，防止请求体里的 ID 和 URL 里的 ID 不一致
		user.setId(id);

		// 更新前检查该 ID 是否存在
		if (id==null){
			return Result.sysError("ID不能为空");
		}
		boolean success = userService.updateById(user);
		return success ? Result.success() : Result.sysError("更新失败");
	}

	@PatchMapping("/{id}/password/reset")
	public Result<String> resetPassword(@PathVariable Long id) {
		// 假设初始密码为 123456，实际开发中建议配合加密工具类
		String defaultPwd = "123456";
		boolean success = userService.resetPassword(id, defaultPwd);
		return success ? Result.success("密码已重置为: " + defaultPwd) : Result.sysError("重置失败");
	}
}
