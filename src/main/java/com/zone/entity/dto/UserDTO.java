package com.zone.entity.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 用户DTO
 * @Date: 2026/3/17 12:09
 * @Version: 1.0
 */
@Data
public class UserDTO {
	private Long id;
	private String username;
	private String password;
	private String realName;
	private String avatar;
	private String phone;
	private Integer status;
	private Date updateTime;
	private List<Long> roleIds;
}
