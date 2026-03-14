package com.zone.entity.base;

import lombok.Data;

/**
 * @Author: JamHoo
 * @Description: 分页查询参数
 * @Date: 2026/3/13 20:32
 * @Version: 1.0
 */
@Data
public class PageQuery extends BaseEntity{
	/**
	 * 当前页码
	 */
	private int pageNum;

	/**
	 * 每页数量
	 */
	private int pageSize=10;
}
