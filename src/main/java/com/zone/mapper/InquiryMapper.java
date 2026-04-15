package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.domain.dto.InquiryPageQueryDTO;
import com.zone.domain.entity.Inquiry;
import com.zone.domain.vo.InquiryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InquiryMapper {

	int insert(Inquiry inquiry);

	int updateById(Inquiry inquiry);

	Inquiry selectById(@Param("id") Long id);

	// B端分页查询
	Page<InquiryVO> getAdminInquiryPage(@Param("dto") InquiryPageQueryDTO dto);

	// 数据库防刷：统计某 IP 当天的提交次数
	@Select("SELECT COUNT(*) FROM biz_inquiry WHERE create_ip = #{ip} AND DATE(create_time) = CURDATE() AND is_deleted = 0")
	int countByIpToday(@Param("ip") String ip);
}