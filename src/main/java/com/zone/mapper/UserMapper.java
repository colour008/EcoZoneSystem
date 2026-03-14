package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.entity.dto.UserPageQueryDTO;
import com.zone.entity.sys.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户映射接口
 * @Date: 2026/3/8 15:26
 * @Version: 1.0
 */
@Mapper
public interface UserMapper {


	// 根据用户名查询用户
	User selectByUsername(String username);

	// 查询用户列表
	List<User> getUserlist();

	// 分页查询用户列表
	Page<User> getUserPage(UserPageQueryDTO dto);

	// 添加用户
	boolean insert(User user);

}
