package com.zone.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 分页查询结果封装类
 * @param <T> 具体的记录类型，如 RoleVO, UserVO 等
 * @Date: 2026/3/13 21:02
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

	/**
	 * 总记录数
	 */
	private long total;

	/**
	 * 当前页数据集合
	 */
	private List<T> records;
}
