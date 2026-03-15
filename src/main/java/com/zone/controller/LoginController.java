package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.entity.dto.UserLoginDTO;
import com.zone.entity.dto.UserRegisterDTO;
import com.zone.entity.vo.LoginResultVO;
import com.zone.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "登录接口", description = "登录接口")
@Slf4j
public class LoginController {

	@Autowired
	private LoginService loginService;

	/**
	 * 登录
	 *
	 * @param dto
	 * @return
	 */
	@PostMapping("/login")
	@Operation(summary = "登录")
	public Result<LoginResultVO> login(@RequestBody UserLoginDTO dto) {
		log.info("用户 {} 尝试登录", dto.getUsername());
		LoginResultVO loginResult = loginService.login(dto.getUsername(), dto.getPassword());
		return Result.success(loginResult);
	}

	/**
	 * 注册
	 *
	 * @param dto
	 * @return
	 */
	@PostMapping("/register")
	@Operation(summary = "注册")
	public Result<String> register(@Validated @RequestBody UserRegisterDTO dto) {
		log.info("用户 {} 尝试注册", dto.getUsername());
		loginService.register(dto);
		return Result.success("注册成功");
	}

}