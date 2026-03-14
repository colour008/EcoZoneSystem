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
}
