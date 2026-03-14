package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.exception.BusinessException;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.mapper.UserMapper;
import com.zone.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
	public PageResult getUserPage(UserPageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<User> page = userMapper.getUserPage(dto);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 添加用户
	 *
	 * @param user
	 * @return boolean
	 */
	@Override
	public boolean addUser(User user) {
		try {
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				log.info("用户{}密码加密完成", user.getUsername());
			} else {
				throw new BusinessException("密码不能为空");
			}
			boolean result = userMapper.insert(user);
			log.info("新增用户{}成功，ID：{}", user.getUsername(), user.getId());
			return result;
		} catch (Exception e) {
			log.error("新增用户失败：{}", e.getMessage(), e);
			throw new BusinessException("新增用户失败：" + e.getMessage());
		}
	}
}
