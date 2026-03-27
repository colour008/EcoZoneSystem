package com.zone.domain.dto;

import com.zone.domain.base.BaseEntity;
import com.zone.domain.vo.MenuVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 菜单权限参数
 * @Date: 2026/3/22 09:09
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO extends BaseEntity implements Serializable {
	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;
	/**
	 * 菜单名称
	 */
	private String menuName;
	/**
	 * 菜单图标
	 */
	private String icon;
	/**
	 * 排序
	 */
	private Integer orderNum;
	/**
	 * 路由地址
	 */
	private String path;
	/**
	 * 组件路径
	 */
	private String component;
	/**
	 * 权限标识
	 */
	private String perms;
	/**
	 * 菜单类型（M目录 C菜单 F按钮）
	 */
	private String type;
	/**
	 * 菜单状态（0隐藏 1显示）
	 */
	private Integer status;
	/**
	 * 是否外链（0是 1否）
	 */
	private Integer isExternal;
	/**
	 * 嵌套子菜单
	 */
	private List<MenuVO> children = new ArrayList<>();
}
