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

		// 1. 【核心逻辑】检查角色是否被用户占用
		int count = roleMapper.countUserRoleByRoleIds(ids);
		if (count > 0) {
			// 发现有用户关联，拒绝删除，抛出业务异常
			log.warn("尝试删除正在使用的角色，操作被拒绝。角色IDs: {}", ids);
			throw new BusinessException("所选角色中存在已分配给用户的角色，请先取消关联后再删除");
		}

		// 2. 执行物理删除
		int rows = roleMapper.deleteByIds(ids);

		// 3. (进阶) 如果以后有角色菜单关联表，这里还要顺便删除 sys_role_menu 里的记录
		// roleMenuMapper.deleteByRoleIds(ids);

		return rows > 0;
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
}
