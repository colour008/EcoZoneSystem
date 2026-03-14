package com.zone.service;

public interface LoginService {
	/**
	 * 登录
	 * @param username 用户名
	 * @param password 密码
	 * @return String
	 */
	String login(String username, String password);
}