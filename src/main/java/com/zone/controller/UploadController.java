package com.zone.controller;

import com.zone.common.response.Result;
import com.zone.util.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传接口", description = "MinIO 文件上传")
@RequiredArgsConstructor
public class UploadController {

	private final MinioUtil minioUtil;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "文件上传")
	public Result<String> upload(@RequestParam("file") MultipartFile file) {
		log.info("开始上传：{}", file.getOriginalFilename());

		if (file.isEmpty()) {
			return Result.sysError("文件不能为空");
		}

		try {
			String url = minioUtil.upload(file.getBytes(), file.getOriginalFilename());
			return Result.success(url);
		} catch (Exception e) {
			log.error("上传失败", e);
			return Result.sysError("上传失败：" + e.getMessage());
		}
	}
}