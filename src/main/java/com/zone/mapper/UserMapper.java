package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.domain.dto.UserPageQueryDTO;
import com.zone.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: JamHoo
 * @Description: 用户映射接口
 * @Date: 2026/3/8 15:26
 * @Version: 1.0
 */
@Mapper
public interface UserMapper {


	/**
	 * 根据用户名查询用户
	 * @param username
	 * @return User
	 */
	User selectByUsername(String username);

	/**
	 * 获取用户列表
	 * @return
	 */
	List<User> getUserlist();

	/**
	 * 分页查询用户列表
	 * @param dto
	 * @return
	 */
	Page<User> getUserPage(UserPageQueryDTO dto);

	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	boolean insert(User user);

	/**
	 * 删除用户
	 * @param ids
	 * @return
	 */
	int deleteByIds(List<Long> ids);

	/**
	 * 动态更新用户信息
	 * @param user 用户实体
	 * @return 影响行数
	 */
	int update(User user);

	/**
	 * 根据ID查询用户
	 * @param id
	 * @return
	 */
	User selectById(Long id);

	/**
	 * 检查用户名是否存在
	 * @param username
	 * @return
	 */
	int checkUsernameExists(@Param("username") String username);

	/**
	 * 查询所有员工
	 * @return
	 */
	List<Map<String, Object>> selectWorkerList();
}
