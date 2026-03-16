package com.zone.service.impl;

import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.entity.dto.UserRegisterDTO;
import com.zone.entity.sys.User;
import com.zone.entity.vo.LoginResultVO;
import com.zone.entity.vo.UserVO;
import com.zone.mapper.UserMapper;
import com.zone.service.LoginService;
import com.zone.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @Author: JamHoo
 * @Description: 登录服务实现类
 * @Date: 2026/3/13 18:37
 * @Version: 1.0
 */

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
			throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
		}

		// 2. 校验密码 (使用你之前配置的 passwordEncoder)
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BusinessException(ResponseCodeEnum.USER_PASSWORD_ERROR);
		}

		// 3. 校验状态
		if (user.getStatus() == 0) {
			throw new BusinessException(ResponseCodeEnum.USER_STATUS_DISABLED);
		}

		// 4. 生成 Token (传入 userId 和 username)
		String token = jwtUtil.generateToken(user.getId(), user.getUsername());

		// 5. 封装返回信息
		UserVO userVO = new UserVO();
		try {
			PropertyUtils.copyProperties(userVO, user);
		} catch (Exception e) {
			throw new BusinessException("用户信息转换失败: " + e.getMessage());
		}
		return LoginResultVO.builder()
				.token(token)
				.user(userVO)
				.build();
	}

	/**
	 * 注册
	 *
	 * @param dto 注册参数
	 */
	@Override
	@Transactional
	public void register(@Validated @RequestBody UserRegisterDTO dto) {

		// 1. 用户名唯一性校验
		if (userMapper.checkUsernameExists(dto.getUsername()) > 0) {
			throw new BusinessException(ResponseCodeEnum.USER_NAME_DUPLICATE);
		}

		// 2. 拷贝属性
		User user = new User();
		try {
			// 注意：PropertyUtils.copyProperties(dest, orig)
			// 如果字段名一致但类型不同（如 String 转 Integer），这里会直接报错
			PropertyUtils.copyProperties(user, dto);
		} catch (Exception e) {
			throw new BusinessException("注册数据处理异常: " + e.getMessage());
		}

		// 3. 加密覆盖
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		// 4. 执行插入
		userMapper.insert(user);
	}
}
