package com.zone.entity.vo;

import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 返回用户信息
 * @Date: 2026/3/15 10:45
 * @Version: 1.0
 */
@Data
public class UserVO {
	private Long id;
	private String username;
	private String realName;
	private String avatar;
	private Integer status;
}
