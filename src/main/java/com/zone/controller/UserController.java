package com.zone.controller;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.common.response.Result;
import com.zone.domain.base.PageResult;
import com.zone.domain.dto.UserDTO;
import com.zone.domain.dto.UserPageQueryDTO;
import com.zone.domain.entity.User;
import com.zone.domain.vo.UserVO;
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
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询用户列表")
	public Result<PageResult<UserVO>> getUserPage(UserPageQueryDTO dto) {
		log.info("分页查询用户列表");
		// Service 内部现在会额外填充 topRoleSort 字段
		PageResult<UserVO> pageResult = userService.getUserPage(dto);
		return Result.success(pageResult);
	}

	/**
	 * 新增用户
	 */
	@PostMapping("/add")
	@Operation(summary = "新增用户")
	public Result<String> addUser(@RequestBody UserDTO userDTO) {
		log.info("新增用户: {}", userDTO.getUsername());
		// 新增用户通常不需要校验等级（因为目标用户还没创建），但可以校验当前人是否有权分配选中的角色
		boolean flag = userService.addUser(userDTO);
		return flag ? Result.success("新增用户成功") : Result.sysError("新增用户失败");
	}

	/**
	 * 删除用户
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "删除用户")
	public Result<String> delete(@RequestBody List<Long> ids, @RequestAttribute("userId") Long currentLoginId) {
		log.info("用户 {} 尝试批量删除用户: {}", currentLoginId, ids);
		// 传入 currentLoginId 进行等级比对校验
		boolean success = userService.deleteByIds(ids, currentLoginId);
		return success ? Result.success("删除成功") : Result.sysError("删除失败");
	}

	/**
	 * 修改用户（管理端）
	 */
	@PutMapping("/{id}")
	@Operation(summary = "修改用户")
	public Result<?> update(@PathVariable Long id, @RequestBody UserDTO userDTO, @RequestAttribute("userId") Long currentLoginId) {
		log.info("用户 {} 尝试修改用户资料: {}", currentLoginId, id);
		if (!id.equals(userDTO.getId())) {
			return Result.bizError(400, "路径ID与请求体ID不一致");
		}
		// 传入 currentLoginId 进行等级比对校验
		boolean success = userService.updateById(userDTO, currentLoginId);
		return success ? Result.success("更新成功") : Result.sysError("更新失败");
	}

	/**
	 * 重置密码
	 */
	@PatchMapping("/{id}/password/reset")
	@Operation(summary = "重置密码")
	public Result<String> resetPassword(@PathVariable Long id, @RequestAttribute("userId") Long currentLoginId) {
		log.info("用户 {} 尝试重置用户 {} 的密码", currentLoginId, id);
		String defaultPwd = "123456";
		// 传入 currentLoginId 进行等级比对校验
		boolean success = userService.resetPassword(id, defaultPwd, currentLoginId);
		return success ? Result.success("密码已重置为: " + defaultPwd) : Result.sysError("重置失败");
	}

	/**
	 * 修改用户状态
	 */
	@PatchMapping("/{id}/status/{status}")
	@Operation(summary = "修改用户状态")
	public Result<Void> changeStatus(
			@PathVariable Long id,
			@PathVariable Integer status,
			@RequestAttribute("userId") Long currentLoginId) {
		log.info("用户 {} 尝试修改用户 {} 的状态为 {}", currentLoginId, id, status);

		// 1. 基本校验：不能停用自己
		if (id.equals(currentLoginId)) {
			return Result.sysError("不能操作自己的账号状态");
		}

		// 2. 调用 Service，传入 currentLoginId 进行等级比对校验
		boolean success = userService.updateStatus(id, status, currentLoginId);
		return success ? Result.success() : Result.sysError("状态修改失败");
	}

	/**
	 * 修改个人资料（用户自己操作）
	 */
	@PatchMapping("/profile")
	@Operation(summary = "修改个人资料")
	public Result<String> updateProfile(@RequestBody UserDTO userDTO, @RequestAttribute("userId") Long currentId) {
		if (currentId == null) {
			return Result.sysError("未登录或登录已失效");
		}

		// 强制设置 ID 为当前登录人 ID，确保用户只能修改自己的资料，防止越权
		userDTO.setId(currentId);

		if (StringUtils.hasText(userDTO.getPassword())) {
			userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		// 这里可以直接调用不需要校验等级的逻辑，或者传入 currentId 自己校验通过
		boolean success = userService.updateById(userDTO, currentId);
		return success ? Result.success("资料更新成功") : Result.sysError("更新失败");
	}

	/**
	 * 查询用户详情
	 */
	@GetMapping("/{id}")
	@Operation(summary = "查询用户详情")
	public Result<UserVO> getById(@PathVariable Long id) {
		User user = userService.getById(id);
		if (user == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}

		List<Long> roleIds = userRoleService.getRoleIdsByUserId(id);

		UserVO vo = new UserVO();
		BeanUtils.copyProperties(user, vo);
		vo.setRoleIds(roleIds);

		return Result.success(vo);
	}
}
