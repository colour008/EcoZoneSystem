package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: TODO
 * @Date: 2026/4/3 21:02
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticePageQueryDTO extends PageQuery {

	private String title;          // 标题

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

	private String publisherName; // 冗余字段--发布人名称
}
