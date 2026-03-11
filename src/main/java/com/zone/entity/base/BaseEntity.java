package com.zone.entity.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: JamHoo
 * @Description: 基础实体类
 * @Date: 2026/3/8 14:30
 * @Version: 1.0
 */
@Data
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;
}
