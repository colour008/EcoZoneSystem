package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.domain.dto.NoticePageQueryDTO;
import com.zone.domain.entity.Notice;
import com.zone.domain.vo.NoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 通知公告Mapper
 * @Author: JamHoo
 * @Date: 2026-4-3
 */
@Mapper
public interface NoticeMapper {

	/**
	 * 新增
	 */
	int insert(Notice notice);

	/**
	 * 修改
	 */
	int updateById(Notice notice);

	/**
	 * 根据ID查询
	 */
	Notice selectById(Long id); // 返回实体类
	NoticeVO getDetailById(Long id); // 返回VO类

	/**
	 * 更新状态 (如：归档)
	 */
	int updateStatus(@Param("id") Long id, @Param("status") Integer status);

	/**
	 * 增加阅读量
	 */
	int incrementViewCount(Long id);

	/**
	 * B端-全量分页查询
	 */
	Page<NoticeVO> getAdminNoticePage(NoticePageQueryDTO dto);

	/**
	 * C端-获取公开分页列表，并关联当前用户的已读状态
	 */
	Page<NoticeVO> getPublicNoticePage(@Param("dto") NoticePageQueryDTO dto, @Param("currentUserId") Long currentUserId);

	/**
	 * 获取指定公告的定向目标用户ID集合 (需关联 biz_notice_target 表)
	 */
	List<Long> getSelectedUserIds(Long noticeId);

}