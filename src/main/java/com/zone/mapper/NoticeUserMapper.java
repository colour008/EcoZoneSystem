package com.zone.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 通知公告关联用户Mapper
 * @Author: JamHoo
 * @Date: 2026-4-3
 */
@Mapper
public interface NoticeUserMapper {

	/**
	 * 根据公告ID清理分发记录（用于重新发布或撤回）
	 */
	int deleteByNoticeId(Long noticeId);

	/**
	 * 批量插入用户收件箱记录 (MyBatis foreach 优化)
	 */
	int batchInsert(@Param("noticeId") Long noticeId, @Param("userIds") List<Long> userIds);

	/**
	 * 将公告标记为当前用户已读
	 */
	int markAsRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);

	/**
	 * 统计当前用户的未读消息总数
	 */
	int countUnreadByUserId(Long userId);
}
