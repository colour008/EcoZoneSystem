package com.zone.domain.entity;

import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 公告实体类 - 对应数据库 biz_notice
 * @Date: 2026-4-3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String title;          // 标题
	private String summary;        // 摘要
	private String coverUrl;       // 封面图
	private String content;        // 正文(LONGTEXT)

	/**
	 * 类型: 1政策推送 2园区动态 3通知公告 4内部通报
	 */
	private Integer type;

	/**
	 * 多端可见性: 0仅内部(B端) 1公开可见(C端同步)
	 */
	private Integer visibility;

	/**
	 * 推送范围: 0全部用户 1指定部分用户
	 */
	private Integer targetScope;

	/**
	 * 状态: 0草稿 1已发布 2已撤回 3已归档
	 */
	private Integer status;

	private Long publisherId;      // 发布人ID
	private LocalDateTime publishTime; // 发布时间
	private Integer viewCount;     // 阅读量
	private Integer isDeleted;     // 逻辑删除

	private String publisherName; // 冗余字段--发布人名称

}