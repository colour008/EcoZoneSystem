package com.zone.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 通知公告定向推送目标配置Mapper (对应表 biz_notice_target)
 * @Author: JamHoo
 * @Date: 2026-4-3
 */
@Mapper
public interface NoticeTargetMapper {

	/**
	 * 根据公告ID清理配置记录
	 */
	int deleteByNoticeId(@Param("noticeId") Long noticeId);

	/**
	 * 批量插入定向推送目标用户
	 */
	int batchInsert(@Param("noticeId") Long noticeId, @Param("userIds") List<Long> userIds);

	/**
	 * 批量删除多个公告对应的配置记录 (用于公告批量删除时清理)
	 */
	int deleteByNoticeIds(@Param("noticeIds") List<Long> noticeIds);

	/**
	 * 根据公告ID查询目标用户ID集合 (用于发布分发或回显)
	 */
	List<Long> selectUserIdsByNoticeId(@Param("noticeId") Long noticeId);

}