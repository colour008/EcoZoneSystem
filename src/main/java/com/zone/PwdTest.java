package com.zone;

import cn.hutool.crypto.digest.BCrypt;

public class PwdTest {
	public static void main(String[] args) {
		// 生成 123456 对应的标准 BCrypt 密码
		String hash = BCrypt.hashpw("123456");
		System.out.println(hash);
	}
}