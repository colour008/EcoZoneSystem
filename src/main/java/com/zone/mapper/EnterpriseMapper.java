package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.domain.dto.EnterprisePageQueryDTO;
import com.zone.domain.entity.Enterprise;
import com.zone.domain.vo.EnterpriseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 企业mapper映射接口
 * @Date: 2026/3/21 14:22
 * @Version: 1.0
 */
@Mapper
public interface EnterpriseMapper {
	List<Enterprise> listAll(); // 获取所有企业列表

	int insert(Enterprise enterprise); // 插入企业信息

	int countByCreditCode(@Param("creditCode") String creditCode); // 根据统一社会信用代码查询数量（防重）

	int updateStatus(Long id, Integer status); // 修改企业状态

	Page<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto); // 获取企业分页列表
}
