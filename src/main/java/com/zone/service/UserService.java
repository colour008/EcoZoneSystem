package com.zone.service;

import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserDTO;
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

	/**
	 * 添加用户
	 * @param userDTO
	 * @return boolean
	 */
	boolean addUser(UserDTO userDTO);

	/**
	 * 删除用户
	 * @param ids
	 * @return boolean
	 */
	boolean deleteByIds(List<Long> ids);

	/**
	 * 修改用户
	 * @param userDTO
	 * @return boolean
	 */
	boolean updateById(UserDTO userDTO);

	/**
	 * 重置密码
	 * @param id
	 * @param defaultPwd
	 * @return boolean
	 */
	boolean resetPassword(Long id, String defaultPwd);

	/**
	 * 修改用户状态
	 * @param id
	 * @param status
	 * @return boolean
	 */
	boolean updateStatus(Long id, Integer status);
}
