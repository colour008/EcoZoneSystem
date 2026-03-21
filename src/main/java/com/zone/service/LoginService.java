package com.zone.service;

import com.zone.domain.dto.UserRegisterDTO;
import com.zone.domain.vo.LoginResultVO;

public interface LoginService {
	/**
	 * 登录
	 * @param username 用户名
	 * @param password 密码
	 * @return String
	 */
	LoginResultVO login(String username, String password);

	/**
	 * 注册
	 * @param dto 注册参数
	 */
	void register(UserRegisterDTO dto);
}