package com.zone.service;

import com.zone.entity.base.PageResult;
import com.zone.entity.dto.UserDTO;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import com.zone.entity.vo.UserVO;

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
	PageResult<UserVO> getUserPage(UserPageQueryDTO dto);

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
	boolean deleteByIds(List<Long> ids, Long currentUserId);

	/**
	 * 修改用户
	 * @param userDTO
	 * @return boolean
	 */
	boolean updateById(UserDTO userDTO, Long currentUserId);

	/**
	 * 重置密码
	 * @param id
	 * @param defaultPwd
	 * @return boolean
	 */
	boolean resetPassword(Long id, String defaultPwd, Long currentUserId);

	/**
	 * 修改用户状态
	 * @param id
	 * @param status
	 * @return boolean
	 */
	boolean updateStatus(Long id, Integer status, Long currentUserId);

	/**
	 * 根据ID查询用户
	 * @param id
	 * @return User
	 */
	User getById(Long id);
}
