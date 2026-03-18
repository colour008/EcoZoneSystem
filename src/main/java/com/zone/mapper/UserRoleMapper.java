package com.zone.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户角色映射接口
 * @Date: 2026/3/8 15:26
 * @Version: 1.0
 */
@Mapper
public interface UserRoleMapper {

	/**
	 * 根据用户ID查询拥有的角色ID集合
	 * @param userId 用户ID
	 * @return 角色ID列表
	 */
	List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

	/**
	 * 批量插入用户角色关系
	 *
	 * @param userId
	 * @param roleIds
	 * @return
	 */
	int insertBatch(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

	/**
	 * 根据用户ID删除用户角色关系
	 *
	 * @param userId
	 * @return
	 */
	int deleteByUserId(Long userId);

	/**
	 * 获取用户的最高角色权限等级（role_sort 最小值）
	 * @param userId 用户ID
	 * @return 最小的 role_sort，若无角色则返回 null
	 */
	Integer selectMinRoleSortByUserId(@Param("userId") Long userId);
}
