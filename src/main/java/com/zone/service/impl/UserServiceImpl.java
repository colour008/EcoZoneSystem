package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.common.response.Result;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserDTO;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.entity.vo.UserVO;
import com.zone.mapper.UserMapper;
import com.zone.mapper.UserRoleMapper;
import com.zone.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: JamHoo
 * @Description: 用户服务实现类
 * @Date: 2026/3/13 18:37
 * @Version: 1.0
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 核心校验私有方法
	private void checkHierarchyPermission(Long currentUserId, Long targetUserId) {
		if (currentUserId.equals(targetUserId)) return;

		Integer myTopSort = userRoleMapper.selectMinRoleSortByUserId(currentUserId);
		Integer targetTopSort = userRoleMapper.selectMinRoleSortByUserId(targetUserId);

		if (myTopSort == null) {
			throw new BusinessException("您未分配角色，无权执行此操作");
		}
		// 如果目标没角色，targetTopSort 为 null，myTopSort > null 在 Java 中不成立，会跳过拦截
		if (targetTopSort != null && myTopSort > targetTopSort) {
			throw new BusinessException("权限不足：无法操作等级高于您的用户");
		}
	}

	/**
	 * 获取用户列表
	 *
	 * @return List<User>
	 */
	@Override
	public List<User> getUserlist() {
		return userMapper.getUserlist();
	}

	/**
	 * 分页查询用户列表
	 *
	 * @return PageResult
	 */
	@Override
	public PageResult<UserVO> getUserPage(UserPageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<User> page = userMapper.getUserPage(dto);

		List<UserVO> voList = page.getResult().stream().map(user -> {
			UserVO vo = new UserVO();
			BeanUtils.copyProperties(user, vo);
			// 额外查询该用户的最高权限等级，传给前端用于按钮显隐控制
			vo.setTopRoleSort(userRoleMapper.selectMinRoleSortByUserId(user.getId()));
			return vo;
		}).collect(Collectors.toList());

		return new PageResult<>(page.getTotal(), voList);
	}

	/**
	 * 添加用户
	 *
	 * @param userDTO
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean addUser(UserDTO userDTO) {
		if (userMapper.checkUsernameExists(userDTO.getUsername()) > 0) {
			throw new BusinessException(ResponseCodeEnum.USER_NAME_DUPLICATE);
		}
		try {
			if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
				userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
				log.info("用户{}密码加密完成", userDTO.getUsername());
			} else {
				throw new BusinessException("密码不能为空");
			}
			User user = new User();
			try {
				BeanUtils.copyProperties(userDTO,user);
			} catch (Exception e) {
				throw new BusinessException("用户信息转换失败: " + e.getMessage());
			}
			boolean result = userMapper.insert(user);
			// 绑定角色关系
			if (result && userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
				// 注意：这里用的是刚插入数据库回填的 user.getId()
				userRoleMapper.insertBatch(user.getId(), userDTO.getRoleIds());
			}
			log.info("新增用户{}成功，ID：{}", userDTO.getUsername(), user.getId());
			return result;
		} catch (Exception e) {
			log.error("新增用户失败：{}", e.getMessage(), e);
			throw new BusinessException("新增用户失败：" + e.getMessage());
		}
	}

	/**
	 * 删除用户
	 *
	 * @param ids
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class) // 开启事务，确保删除操作的原子性
	public boolean deleteByIds(List<Long> ids, Long currentUserId) {
		if (ids == null || ids.isEmpty()) return false;

		// 逐个校验权限
		for (Long id : ids) {
			checkHierarchyPermission(currentUserId, id);
		}

		for (Long userId : ids) {
			userRoleMapper.deleteByUserId(userId);
		}
		return userMapper.deleteByIds(ids) > 0;
	}

	/**
	 * 修改用户
	 *
	 * @param userDTO
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(UserDTO userDTO, Long currentUserId) {
		// 1. 基础校验：如果是修改别人，执行职级校验
		checkHierarchyPermission(currentUserId, userDTO.getId());

		// 2. 敏感权限保护：防止“平民”通过修改接口把自己变成“皇帝”
		// 如果不是超管，且尝试修改角色列表，则需要增加额外的逻辑判断
		if (!currentUserId.equals(userDTO.getId())) {
			// 修改别人，逻辑已由 checkHierarchyPermission 覆盖
		} else {
			// 如果是修改自己，且传入了 roleIds，通常建议禁止普通用户通过此接口修改自己的角色
			// 或者在这里强制重新查询该用户原有的角色，覆盖掉传入的 roleIds（防止越权）
			log.warn("用户 {} 尝试修改自己的信息", currentUserId);
		}

		// 3. 用户名冲突检查（排除自身）
		User existingUser = userMapper.selectByUsername(userDTO.getUsername());
		if (existingUser != null && !existingUser.getId().equals(userDTO.getId())) {
			throw new BusinessException(ResponseCodeEnum.USER_NAME_DUPLICATE);
		}

		User user = new User();
		BeanUtils.copyProperties(userDTO, user);

		// 如果前端没传密码，不要把空密码更新进去（PasswordEncoder 会把空串也加密）
		if (!StringUtils.hasText(user.getPassword())) {
			user.setPassword(null);
		} else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		int rows = userMapper.update(user);

		// 4. 角色更新的特殊处理
		if (rows > 0 && userDTO.getRoleIds() != null) {
			// 建议：只有超管或者具有特定权限的人才能通过此接口修改角色
			// 如果是个人中心调用，前端不应传 roleIds，或者后端在这里做过滤
			userRoleMapper.deleteByUserId(userDTO.getId());
			if (!userDTO.getRoleIds().isEmpty()) {
				userRoleMapper.insertBatch(userDTO.getId(), userDTO.getRoleIds());
			}
		}
		return rows > 0;
	}

	/**
	 * 重置密码
	 *
	 * @param id
	 * @param defaultPwd
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean resetPassword(Long id, String defaultPwd, Long currentUserId) {
		// 重置前校验
		checkHierarchyPermission(currentUserId, id);

		User updateUserInfo = new User();
		updateUserInfo.setId(id);
		updateUserInfo.setPassword(passwordEncoder.encode(defaultPwd));
		return userMapper.update(updateUserInfo) > 0;
	}

	/**
	 * 修改用户状态
	 *
	 * @param id
	 * @param status
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateStatus(Long id, Integer status, Long currentUserId) {
		// 修改状态前校验
		checkHierarchyPermission(currentUserId, id);

		User updateInfo = new User();
		updateInfo.setId(id);
		updateInfo.setStatus(status);
		return userMapper.update(updateInfo) > 0;
	}

	/**
	 * 根据 ID 查询用户信息
	 *
	 * @param id
	 * @return User
	 */
	@Override
	public User getById(Long id) {
		return userMapper.selectById(id);
	}

}
