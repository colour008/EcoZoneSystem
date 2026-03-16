package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.service.UserService;
import com.zone.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
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

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 获取用户列表
	 *
	 * @return List<User>
	 */
	@GetMapping("/list")
	@Operation(summary = "获取用户列表", description = "获取用户列表")
	public Result<List<User>> getUserlist() {
		log.info("获取用户列表");
		List<User> userlist = userService.getUserlist();
		if (userlist != null) {
			return Result.success(userlist);
		}
		return Result.sysError("获取用户列表失败");
	}

	/**
	 * 分页查询用户列表
	 *
	 * @param dto
	 * @return PageResult
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询用户列表", description = "分页查询用户列表")
	public Result<PageResult> getUserPage(UserPageQueryDTO dto) {
		log.info("分页查询用户列表");
		PageResult pageResult = userService.getUserPage(dto);
		if (pageResult != null) {
			return Result.success(pageResult);
		}
		return Result.sysError("分页查询用户列表失败");
	}

	/**
	 * 添加用户
	 *
	 * @param user
	 * @return boolean
	 */
	@PostMapping("/add")
	@Operation(summary = "添加用户", description = "添加用户")
	public Result<String> addUser(@RequestBody User user) {
		log.info("添加用户");
		boolean flag = userService.addUser(user);
		if (flag) {
			return Result.success("添加用户成功");
		}
		return Result.sysError("添加用户失败");
	}

	/**
	 * 删除用户
	 *
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
	 *
	 * @param id
	 * @param user
	 * @return boolean
	 */
	@PutMapping("/{id}")
	public Result<String> update(@PathVariable Long id, @RequestBody User user) {
		// 强制设置对象 ID，防止请求体里的 ID 和 URL 里的 ID 不一致
		user.setId(id);
		// 更新前检查该 ID 是否存在
		if (id == null) {
			return Result.sysError("ID不能为空");
		}
		boolean success = userService.updateById(user);
		return success ? Result.success() : Result.sysError("更新失败");
	}

	/**
	 * 重置密码
	 *
	 * @param id
	 * @return boolean
	 */
	@PatchMapping("/{id}/password/reset")
	public Result<String> resetPassword(@PathVariable Long id) {
		// 初始密码为 123456
		String defaultPwd = "123456";
		boolean success = userService.resetPassword(id, defaultPwd);
		return success ? Result.success("密码已重置为: " + defaultPwd) : Result.sysError("重置失败");
	}

	/**
	 * 修改用户状态
	 *
	 * @param id
	 * @param status
	 * @return boolean
	 */
	@PatchMapping("/{id}/status/{status}")
	public Result<Void> changeStatus(
			@PathVariable Long id,
			@PathVariable Integer status,
			@RequestAttribute("userId") Long currentLoginId) {
		// 1. 不能操作自己的账号
		if (id.equals(currentLoginId)) {
			return Result.sysError("不能操作自己的账号");
		}
		// 2. 参数合法性基本校验
		if (status != 0 && status != 1) {
			return Result.sysError("非法状态值");
		}
		// 3. 调用 Service
		boolean success = userService.updateStatus(id, status);
		return success ? Result.success() : Result.sysError("状态修改失败");
	}

	/**
	 * 修改用户资料
	 *
	 * @param user
	 * @return boolean
	 */
	@PatchMapping("/profile")
	public Result<String> updateProfile(@RequestBody User user,
	                                    //从 Request 属性中获取拦截器解析好的 ID
	                                    @RequestAttribute("userId") Long currentId
	) {
		if (currentId == null) {
			return Result.sysError("未登录或登录已失效");
		}
		// 强制将 ID 设为当前登录人的 ID，防止越权修改
		user.setId(currentId);
		// 如果修改了密码，需要重新加密
		if (StringUtils.hasText(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		boolean success = userService.updateById(user);
		return success ? Result.success("资料更新成功") : Result.sysError("更新失败");
	}
}
