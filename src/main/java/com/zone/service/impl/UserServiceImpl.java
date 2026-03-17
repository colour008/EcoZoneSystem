package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserDTO;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.entity.vo.UserVO;
import com.zone.mapper.UserMapper;
import com.zone.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private PasswordEncoder passwordEncoder;

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
			BeanUtils.copyProperties(user,vo);
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
	public boolean deleteByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}
		// deleteByIds 返回的是受影响的行数
		int rows = userMapper.deleteByIds(ids);
		return rows > 0;
	}

	/**
	 * 修改用户
	 *
	 * @param userDTO
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(UserDTO userDTO) {
		Long id = userDTO.getId();
		User existUser = userMapper.selectById(id);
		if (existUser == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}

		User user = new User();
		try {
			BeanUtils.copyProperties(userDTO,user);
		} catch (Exception e) {
			throw new BusinessException("用户信息转换失败: " + e.getMessage());
		}
		// 2. 执行更新
		int rows = userMapper.update(user);

		// 3. 返回是否成功
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
	public boolean resetPassword(Long id, String defaultPwd) {
		// 1. 先根据 ID 查询用户信息（用于获取用户名）
		User user = userMapper.selectById(id);
		if (user == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}
		// 2. 准备更新对象
		User updateUserInfo = new User();
		updateUserInfo.setId(id);
		// 确保加密前 defaultPwd 不为 null
		updateUserInfo.setPassword(passwordEncoder.encode(defaultPwd));
		// 3. 执行更新
		int rows = userMapper.update(updateUserInfo);
		if (rows > 0) {
			// 4. 此时可以使用 existUser.getUsername() 记录日志了
			log.info("用户 [ID: {}, 账号: {}] 密码重置完成", id, user.getUsername());
		}
		return rows > 0;
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
	public boolean updateStatus(Long id, Integer status) {
		// 1. 检查用户是否存在（防御性编程）
		User user = userMapper.selectById(id);
		if (user == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}
		// 2. 构造瘦身后的更新对象，减少数据库压力
		User updateInfo = new User();
		updateInfo.setId(id);
		updateInfo.setStatus(status);
		// 3. 执行更新（复用你现有的 update 方法）
		int rows = userMapper.update(updateInfo);
		if (rows > 0) {
			log.info("用户 [ID: {}] 状态已变更为: {}", id, status == 1 ? "正常" : "停用");
		}
		return rows > 0;
	}
}
