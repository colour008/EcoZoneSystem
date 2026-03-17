package com.zone.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
	/**
	 * 根据用户ID查询所有权限标识(perms)
	 */
	List<String> selectPermsByUserId(@Param("userId") Long userId);
}
