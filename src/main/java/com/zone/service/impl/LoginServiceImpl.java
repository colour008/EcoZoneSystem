package com.zone.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.entity.sys.User;
import com.zone.mapper.UserMapper;
import com.zone.service.LoginService;
import com.zone.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

	@Resource
	private JwtUtil jwtUtil;

	@Resource
	private UserMapper userMapper;

	@Override
	public String login(String username, String password) {
		// 1. 根据用户名查库
		User user = userMapper.selectByUsername(username);

		// 2. 用户不存在
		if (user == null) {
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}

		// 3. 密码错误
		boolean pass = BCrypt.checkpw(password, user.getPassword());
		if (!pass) {
			throw new BusinessException(ResponseCodeEnum.USER_PASSWORD_ERROR);
		}

		// 4. 账号禁用
		if (user.getStatus() == 0) {
			throw new BusinessException(ResponseCodeEnum.USER_STATUS_DISABLED);
		}

		// 5. 生成TOKEN
		return jwtUtil.generateToken(user.getUsername());
	}
}
