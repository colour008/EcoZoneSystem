package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.entity.dto.UserLoginDTO;
import com.zone.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "登录接口", description = "登录接口")
public class LoginController {

	@Resource
	private LoginService loginService;

	@PostMapping("/login")
	@Operation(summary = "登录")
	public Result<Map<String, String>> login(@RequestBody UserLoginDTO dto) {
		String token = loginService.login(dto.getUsername(), dto.getPassword());
		return Result.success(Map.of("token", token));
	}
}