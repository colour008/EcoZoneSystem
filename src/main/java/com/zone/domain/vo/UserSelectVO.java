package com.zone.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: 用户下拉选择项
 * @Date: 2026/4/4 11:34
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSelectVO {
	private Long id;
	private String realName;
}
