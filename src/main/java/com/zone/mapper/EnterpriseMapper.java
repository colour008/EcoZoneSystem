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

	int countByCreditCode(@Param("creditCode") String creditCode, @Param("excludeId") Long excludeId); // 根据信用代码查询企业（用于唯一性校验）

	int updateAuditStatus(@Param("id") Long id, @Param("status") Integer status, @Param("auditOpinion") String auditOpinion, @Param("auditorId") Long auditorId);// 修改企业状态

	Page<EnterpriseVO> getEnterprisePage(EnterprisePageQueryDTO dto); // 获取企业分页列表

	int updateById(Enterprise enterprise); // 修改企业信息

	List<Enterprise> listAllByUserId(@Param("userId") Long userId); // 根据用户ID查询企业列表

	EnterpriseVO getById(Long id); // 获取企业详情

	int deleteByIds(@Param("ids") List<Long> ids); // 删除企业

	int countPendingApplications(); // 获取待审核企业数量

	/**
	 * 根据用户ID和状态列表统计数量
	 * @param userId 用户ID
	 * @param statusList 状态列表
	 * @return 统计结果
	 */
	int countByUserIdAndStatus(@Param("userId") Long userId, @Param("statusList") List<Integer> statusList);
}
