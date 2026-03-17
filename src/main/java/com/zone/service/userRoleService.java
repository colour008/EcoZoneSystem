package com.zone.service;


import java.util.List;

public interface userRoleService {

	/**
	 * 根据用户id获取角色id列表
	 * @param id
	 * @return
	 */
	List<Long> getRoleIdsByUserId(Long id);
}
