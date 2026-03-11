package com.zone.mapper;

import com.zone.entity.sys.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: JamHoo
 * @Description: TODO
 * @Date: 2026/3/8 15:26
 * @Version: 1.0
 */
@Mapper
public interface UserMapper {

	// 根据用户名查询用户
	User selectByUsername(String username);
}
