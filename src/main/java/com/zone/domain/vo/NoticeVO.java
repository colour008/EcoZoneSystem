package com.zone.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description: 通知公告VO
 * @Author: JamHoo
 * @date: 2026-4-3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeVO {
	private Long id;
	private String title;
	private String summary;

	// 如果没有封面，返回空字符串而不是null
	private String coverUrl;
	// 手写 Getter 确保不返回 null（或者在 Setter 里判断）
	public String getCoverUrl() {
		return coverUrl == null ? "" : coverUrl;
	}

	private String content;        // 正文(LONGTEXT)

	private Integer type;
	private Integer status;
	private Integer visibility;
	/**
	 * 发布时间
	 */
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime publishTime;
	private Integer targetScope;
	private Long publisherId;      // 发布人ID
	private Integer viewCount;
	private Integer isDeleted;     // 逻辑删除
	private Boolean isRead; // C端展示时，标识当前用户是否已读
	/**
	 * 创建时间
	 */
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime updateTime;
	private String publisherName = ""; // 冗余字段--发布人名称
	public String getPublisherName() {
		return publisherName == null ? "" : publisherName;
	}
}