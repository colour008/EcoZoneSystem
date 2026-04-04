package com.zone.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description: 通知公告用户关联实体类
 * @Author: JamHoo
 * @date: 2026-4-3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeUser {
	private Long id;
	private Long noticeId;
	private Long userId;
	private Integer isRead;        // 0未读 1已读
	private LocalDateTime readTime;
	private LocalDateTime createTime;
}