package com.zone.service.impl;

import com.zone.mapper.UserRoleMapper;
import com.zone.service.userRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户角色服务实现类
 * @Date: 2026/3/17 15:59
 * @Version: 1.0
 */
@Service
@Slf4j
public class userRoleServiceImpl implements userRoleService {

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Override
	public List<Long> getRoleIdsByUserId(Long id) {
		// 1. 防御性校验
		if (id == null) {
			return Collections.emptyList();
		}

		// 2. 调用 Mapper 查询
		List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(id);

		// 3. 记录日志（可选，方便排查回显问题）
		log.debug("查询用户[ID:{}]的角色关联，结果数量: {}", id, roleIds.size());

		return roleIds;
	}
}
