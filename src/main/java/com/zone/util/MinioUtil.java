package com.zone.util;

import cn.hutool.core.lang.UUID;
import com.zone.common.enums.ResponseCodeEnum;
import com.zone.common.exception.BusinessException;
import com.zone.entity.base.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioUtil {

	private final MinioClient minioClient;
	private final MinioProperties minioProperties;

	public String upload(byte[] content, String originalFilename) {
		try {
			if (content == null || content.length == 0) {
				throw new BusinessException(ResponseCodeEnum.PARAM_EMPTY, "上传文件不能为空");
			}
			if (originalFilename == null || !originalFilename.contains(".")) {
				throw new BusinessException(ResponseCodeEnum.PARAM_ERROR, "文件名格式错误");
			}

			String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
			String objectName = UUID.randomUUID().toString().replace("-", "") + suffix;

			minioClient.putObject(
					PutObjectArgs.builder()
							.bucket(minioProperties.getBucketName())
							.object(objectName)
							.stream(new ByteArrayInputStream(content), content.length, -1)
							.contentType(getContentType(suffix))
							.build()
			);

			return minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + objectName;
		} catch (Exception e) {
			log.error("MinIO上传失败", e);
			throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "文件上传失败：" + e.getMessage());
		}
	}

	private String getContentType(String suffix) {
		return switch (suffix.toLowerCase()) {
			case ".jpg", ".jpeg" -> "image/jpeg";
			case ".png" -> "image/png";
			case ".gif" -> "image/gif";
			case ".svg" -> "image/svg+xml";
			case ".pdf" -> "application/pdf";
			case ".txt" -> "text/plain";
			case ".mp4" -> "video/mp4";
			default -> "application/octet-stream";
		};
	}
}