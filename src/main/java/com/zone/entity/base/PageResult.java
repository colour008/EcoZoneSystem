package com.zone.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 分页查询结果封装类
 * @Date: 2026/3/13 21:02
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

	private long total; //总记录数

	private List records; //当前页数据集合

}
