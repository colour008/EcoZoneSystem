package com.zone.entity.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: JamHoo
 * @Description: MinIO 配置
 * @Date: 2026/3/16 21:05
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {

	private String endpoint;
	private String accessKey;
	private String secretKey;
	private String bucketName;

}
