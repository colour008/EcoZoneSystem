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

	/**
	 * 根据信用代码查询企业（用于唯一性校验）
	 * @param creditCode 信用代码
	 * @param excludeId 需要排除的企业ID (可为 null)
	 * @return 匹配的企业数量
	 */
	int countByCreditCode(@Param("creditCode") String creditCode, @Param("excludeId") Long excludeId);

	int updateStatus(Long id, Integer status); // 修改企业状态

	Page<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto); // 获取企业分页列表

	int updateById(Enterprise enterprise); // 修改企业信息
}
