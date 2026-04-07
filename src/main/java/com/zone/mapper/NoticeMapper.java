package com.zone.mapper;

import com.github.pagehelper.Page;
import com.zone.domain.dto.NoticePageQueryDTO;
import com.zone.domain.entity.Notice;
import com.zone.domain.vo.NoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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
	 * 根据ID集合查询公告列表
	 */
	List<Notice> selectByIds(@Param("ids") List<Long> ids);

	/**
	 * 批量逻辑删除公告
	 */
	int logicalDeleteByIds(@Param("ids") List<Long> ids);

	/**
	 * 获取上一篇（同类型、已发布、发布时间更早的第一条）
	 */
	Notice selectPrevNotice(@Param("type") Integer type, @Param("publishTime") LocalDateTime publishTime);

	/**
	 * 获取下一篇（同类型、已发布、发布时间更晚的第一条）
	 */
	Notice selectNextNotice(@Param("type") Integer type, @Param("publishTime") LocalDateTime publishTime);

}