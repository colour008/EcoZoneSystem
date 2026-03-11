package com.zone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.zone.mapper")
public class ZoneApplication {
	public static void main(String[] args) {
		SpringApplication.run(ZoneApplication.class, args);
		System.out.println("系统启动成功✅️");
		System.out.println("API文档访问1️⃣:http://localhost:8080/swagger-ui/index.html");
		System.out.println("API文档访问2️⃣:http://localhost:8080/doc.html");
	}

}
