package com.zone.service.impl;

import com.zone.common.exception.BusinessException;
import com.zone.entity.sys.User;
import com.zone.entity.vo.LoginResultVO;
import com.zone.entity.vo.UserVO;
import com.zone.mapper.UserMapper;
import com.zone.service.LoginService;
import com.zone.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 登录
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return TOKEN
	 */
	@Override
	public LoginResultVO login(String username, String password) {
		// 1. 根据用户名查询用户
		User user = userMapper.selectByUsername(username);
		if (user == null) {
			throw new BusinessException("用户不存在");
		}

		// 2. 校验密码 (使用你之前配置的 passwordEncoder)
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BusinessException("密码错误");
		}

		// 3. 校验状态
		if (user.getStatus() == 0) {
			throw new BusinessException("账号已停用，请联系管理员");
		}

		// 4. 生成 Token (传入 userId 和 username)
		String token = jwtUtil.generateToken(user.getId(), user.getUsername());

		// 5. 封装返回信息
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(user, userVO); // 使用 Spring 的工具类拷贝属性

		return LoginResultVO.builder()
				.token(token)
				.user(userVO)
				.build();
	}
}
