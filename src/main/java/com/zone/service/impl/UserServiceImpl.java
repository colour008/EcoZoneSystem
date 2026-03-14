package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.mapper.UserMapper;
import com.zone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户服务实现类
 * @Date: 2026/3/13 18:37
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

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
}
