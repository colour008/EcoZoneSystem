package com.zone.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.entity.base.PageResult;
import com.zone.entity.dto.RoleDTO;
import com.zone.entity.dto.RolePageQueryDTO;
import com.zone.entity.sys.Role;
import com.zone.entity.vo.RoleVO;
import com.zone.mapper.RoleMapper;
import com.zone.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: JamHoo
 * @Description: 角色服务实现类
 * @Date: 2026/3/16 21:16
 * @Version: 1.0
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;


	/**
	 * 新增角色
	 *
	 * @param roleDTO
	 * @return boolean
	 */
	@Override
	public boolean addRole(RoleDTO roleDTO) {
		// 检查角色编码是否已存在
		String roleCode = roleDTO.getRoleCode();
		Role existRole = roleMapper.selectByRoleCode(roleCode);
		if (Objects.nonNull(existRole)) {
			log.error("新增角色失败，角色编码已存在：{}", roleCode);
			throw new BusinessException(ResponseCodeEnum.ROLE_CODE_EXIST);
		}
		// 属性拷贝 & 插入数据库
		Role role = new Role();
		try {
			BeanUtils.copyProperties(roleDTO, role);
			return roleMapper.insert(role) > 0;
		} catch (Exception e) {
			log.error("属性拷贝异常：{}", e.getMessage());
			throw new BusinessException(ResponseCodeEnum.ROLE_ADD_FAILED);
		}
	}

	/**
	 * 分页查询角色列表
	 *
	 * @param dto
	 * @return PageResult
	 */
	@Override
	public PageResult<RoleVO> getRolePage(RolePageQueryDTO dto) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		Page<Role> page = roleMapper.getRolePage(dto);

		// 转换
		List<RoleVO> voList = page.getResult().stream().map(role -> {
			RoleVO vo = new RoleVO();
			BeanUtils.copyProperties(role, vo);
			return vo;
		}).collect(Collectors.toList());

		// 返回时指定泛型类型
		return new PageResult<>(page.getTotal(), voList);
	}

	/**
	 * 修改角色
	 *
	 * @param roleDTO
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(RoleDTO roleDTO) {
		Long id = roleDTO.getId();
		Role existRole = roleMapper.selectById(id);
		if (existRole == null) {
			throw new BusinessException(ResponseCodeEnum.ROLE_NOT_EXIST);
		}

		String roleCode = roleDTO.getRoleCode();
		Role codeExist = roleMapper.selectByRoleCodeExcludeId(roleCode, id);
		if (Objects.nonNull(codeExist)) {
			throw new BusinessException(ResponseCodeEnum.ROLE_CODE_EXIST);
		}

		Role role = new Role();
		BeanUtils.copyProperties(roleDTO, role);
		int rows = roleMapper.update(role);
		return rows > 0;
	}

	/**
	 * 删除角色
	 *
	 * @param ids 支持单一删除和批量删除
	 * @return boolean
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}

		// 1. 检查角色是否被用户占用
		int count = roleMapper.countUserRoleByRoleIds(ids);
		if (count > 0) {
			throw new com.zone.common.exception.BusinessException("选中的角色中有关联用户，无法删除");
		}
		// 同时删除角色与菜单的关联（防止脏数据）
		for (Long roleId : ids) {
			roleMapper.deleteRoleMenusByRoleId(roleId);
		}
		return roleMapper.deleteByIds(ids) > 0;
	}

	/**
	 * 查询所有角色
	 *
	 * @return List<Role>
	 */
	@Override
	public List<Role> listAll() {
		return roleMapper.selectAllActiveRoles();
	}

	/**
	 * 更新角色权限
	 *
	 * @Transactional 保证删除和插入是一个整体，要么全成功，要么全失败
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateRoleMenus(Long roleId, List<Long> menuIds) {
		// 1. 物理删除该角色在关联表里的所有旧数据
		roleMapper.deleteRoleMenusByRoleId(roleId);

		// 2. 如果前端传过来的 ID 列表不为空，则进行批量插入
		if (menuIds != null && !menuIds.isEmpty()) {
			roleMapper.insertRoleMenus(roleId, menuIds);
			log.info("角色ID: {} 权限更新成功，关联节点数: {}", roleId, menuIds.size());
		} else {
			log.warn("角色ID: {} 清空了所有权限", roleId);
		}
	}
}
