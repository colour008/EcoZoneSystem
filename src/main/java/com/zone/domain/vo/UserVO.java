package com.zone.domain.vo;

import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 返回用户信息
 * @Date: 2026/3/15 10:45
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO extends BaseEntity {
	private String username;
	private String realName;
	private String phone;
	private String avatar;
	private Integer status;
	private String roleName;
	private List<Long> roleIds;
	// --- 新增字段 ---
	/**
	 * 该用户的最高角色权限等级（role_sort 最小值）
	 */
	private Integer topRoleSort;
}
