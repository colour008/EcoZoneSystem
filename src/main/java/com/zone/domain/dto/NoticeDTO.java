package com.zone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 通知公告DTO
 * @Date: 2026-4-3
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {
	private Long id;
	private String title;
	private String summary;
	private String coverUrl;
	private String content;
	private Integer type;
	private Integer visibility;
	private Integer targetScope;
	private List<Long> targetUserIds; // 仅当 targetScope=1 时有效
}