package com.zone.service.impl;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.entity.dto.UserRegisterDTO;
import com.zone.entity.sys.Role;
import com.zone.entity.sys.User;
import com.zone.entity.vo.LoginResultVO;
import com.zone.entity.vo.UserVO;
import com.zone.mapper.MenuMapper;
import com.zone.mapper.RoleMapper;
import com.zone.mapper.UserMapper;
import com.zone.mapper.UserRoleMapper;
import com.zone.service.LoginService;
import com.zone.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 登录服务实现类
 * @Date: 2026/3/13 18:37
 * @Version: 1.0
 */

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 登录
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return TOKEN
	 */
	@Override
	public LoginResultVO login(String username, String password) {
		// 1. 根据用户名查询用户
		User user = userMapper.selectByUsername(username);
		if (user == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}

		// 2. 校验密码
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BusinessException(ResponseCodeEnum.USER_PASSWORD_ERROR);
		}

		// 3. 校验状态
		if (user.getStatus() == 0) {
			throw new BusinessException(ResponseCodeEnum.USER_STATUS_DISABLED);
		}

		// 4. 查询角色、权限以及最高等级
		// 查询角色编码列表 (如: ["ROLE_ADMIN"])
		List<String> roles = roleMapper.getRoleCodesByUserId(user.getId());

		// 查询权限标识列表 (如: ["user:add"])
		List<String> permissions = menuMapper.selectPermsByUserId(user.getId());

		// --- 新增：查询当前登录用户的最高角色权重 (role_sort 最小值) ---
		Integer topRoleSort = userRoleMapper.selectMinRoleSortByUserId(user.getId());

		// 5. 生成 Token
		String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

		// 6. 封装返回
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(user, userVO);

		// --- 关键：将最高等级塞入返回给前端的 userVO 中 ---
		userVO.setTopRoleSort(topRoleSort);

		return LoginResultVO.builder()
				.token(token)
				.user(userVO)           // 这里面的 userVO 现在包含了 topRoleSort
				.roles(roles)
				.permissions(permissions)
				.build();
	}

	/**
	 * 注册
	 *
	 * @param dto 注册参数
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(@Validated @RequestBody UserRegisterDTO dto) {

		// 1. 用户名唯一性校验
		if (userMapper.checkUsernameExists(dto.getUsername()) > 0) {
			throw new BusinessException(ResponseCodeEnum.USER_NAME_DUPLICATE);
		}

		// 2. 创建用户并插入
		User user = new User();
		try {
			BeanUtils.copyProperties(dto, user);
			user.setPassword(passwordEncoder.encode(dto.getPassword()));
			user.setStatus(1); // 默认启用
			user.setCreateTime(LocalDateTime.now());
			userMapper.insert(user); // 执行完后，user.getId() 就有值了
		} catch (Exception e) {
			throw new BusinessException("注册失败: " + e.getMessage());
		}

		// 3. 分配默认角色，默认角色编码为 "ROLE_ENTERPRISE"
		Role defaultRole = roleMapper.selectByRoleCode("ROLE_ENTERPRISE");
		if (defaultRole != null) {
			// 构造一个 List 传入我们之前写好的批量插入方法
			userRoleMapper.insertBatch(user.getId(), Collections.singletonList(defaultRole.getId()));
			log.info("用户[{}]注册成功，已自动分配默认角色: {}", dto.getUsername(), defaultRole.getRoleName());
		} else {
			// 这是一个警告，但不一定要抛异常，也可以根据业务决定是否强制要求有角色
			log.warn("注册中心未找到默认角色[ROLE_ENTERPRISE]，用户暂时无角色");
		}
	}
}
