package com.zone.service;

import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户服务接口
 * @Date: 2026/3/13 18:37
 * @Version: 1.0
 */

public interface UserService {

	/**
	 * 获取用户列表
	 * @return List<User>
	 */
	List<User> getUserlist();

	/**
	 * 分页查询用户列表
	 * @param dto
	 * @return PageResult
	 */
	PageResult getUserPage(UserPageQueryDTO dto);
}
