package com.zone.controller;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.common.response.Result;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserDTO;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.entity.vo.UserVO;
import com.zone.service.UserService;
import com.zone.service.userRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
	private userRoleService userRoleService;

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
	public Result<PageResult<UserVO>> getUserPage(UserPageQueryDTO dto) {
		log.info("分页查询用户列表");
		PageResult<UserVO> pageResult = userService.getUserPage(dto);
		return Result.success(pageResult);
	}

	/**
	 * 新增用户
	 *
	 * @param userDTO
	 * @return Result<String>
	 */
	@PostMapping("/add")
	@Operation(summary = "新增用户", description = "新增用户")
	public Result<String> addUser(@RequestBody UserDTO userDTO) {
		log.info("新增用户");
		boolean flag = userService.addUser(userDTO);
		if (flag) {
			return Result.success("新增用户成功");
		}
		return Result.sysError("新增用户失败");
	}

	/**
	 * 删除用户
	 *
	 * @param ids 支持单一删除和批量删除
	 * @return boolean
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "删除用户", description = "删除用户")
	public Result<String> delete(@RequestBody List<Long> ids) {
		boolean success = userService.deleteByIds(ids);
		return success ? Result.success("删除成功") : Result.sysError("删除失败或数据不存在");
	}

	/**
	 * 修改用户
	 *
	 * @param id
	 * @param userDTO
	 * @return boolean
	 */
	@PutMapping("/{id}")
	@Operation(summary = "修改用户", description = "修改用户")
	public Result<?> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
		log.info("修改用户");
		if (!id.equals(userDTO.getId())) {
			return Result.bizError(400, "路径ID与请求体ID不一致");
		}
		boolean success = userService.updateById(userDTO);
		return success ? Result.success() : Result.sysError("更新失败");
	}

	/**
	 * 重置密码
	 *
	 * @param id
	 * @return boolean
	 */
	@PatchMapping("/{id}/password/reset")
	@Operation(summary = "重置密码", description = "重置密码")
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
	@Operation(summary = "修改用户状态", description = "修改用户状态")
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
	 * @param userDTO
	 * @return boolean
	 */
	@PatchMapping("/profile")
	@Operation(summary = "修改用户资料", description = "修改用户资料")
	public Result<String> updateProfile(@RequestBody UserDTO userDTO,
	                                    //从 Request 属性中获取拦截器解析好的 ID
	                                    @RequestAttribute("userId") Long currentId
	) {
		if (currentId == null) {
			return Result.sysError("未登录或登录已失效");
		}
		// 如果修改了密码，需要重新加密
		if (StringUtils.hasText(userDTO.getPassword())) {
			userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}
		boolean success = userService.updateById(userDTO);
		return success ? Result.success("资料更新成功") : Result.sysError("更新失败");
	}

	/**
	 * 查询用户详情
	 *
	 * @param id
	 * @return UserDTO
	 */
	@GetMapping("/{id}")
	@Operation(summary = "查询用户详情")
	public Result<UserDTO> getById(@PathVariable Long id) {
		// 1. 查基础信息
		User user = userService.getById(id);
		if (user == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}

		// 2. 查角色ID列表
		List<Long> roleIds = userRoleService.getRoleIdsByUserId(id);

		// 3. 封装
		UserDTO dto = new UserDTO();
		BeanUtils.copyProperties(user, dto);
		dto.setRoleIds(roleIds);

		return Result.success(dto);
	}
}
