package com.zone.service;

import com.zone.domain.base.PageResult;
import com.zone.domain.dto.NoticeDTO;
import com.zone.domain.dto.NoticePageQueryDTO;
import com.zone.domain.vo.NoticeVO;
import com.zone.domain.vo.UserSelectVO;

import java.util.List;

/**
 * @Author: JamHoo
 * @Description: 通知公告服务接口
 * @Date: 2026/4/3 21:04
 * @Version: 1.0
 */
public interface NoticeService {

	// ================== B端：园区管理接口 ==================

	/**
	 * B端-新增或修改公告(草稿)
	 *
	 * @param dto
	 * @return
	 */
	boolean saveOrUpdateNotice(NoticeDTO dto);

	/**
	 * B端-发布公告
	 *
	 * @param id
	 * @return
	 */
	boolean publish(Long id);

	/**
	 * B端-撤回公告
	 *
	 * @param id
	 * @return
	 */
	boolean recall(Long id);

	/**
	 * B端-归档公告
	 *
	 * @param id
	 * @return
	 */
	boolean archive(Long id);

	/**
	 * B端-全量分页列表
	 *
	 * @param dto
	 * @return
	 */
	PageResult<NoticeVO> getAdminPage(NoticePageQueryDTO dto);

	// ================== C端：企业用户接口 ==================

	/**
	 * C端-获取政策/动态/公告列表
	 * @param dto
	 * @return
	 */
	PageResult<NoticeVO> getPublicPage(NoticePageQueryDTO dto);

	/**
	 * C端-获取公告详情
	 * @param id
	 * @return
	 */
	NoticeVO getDetail(Long id);

	/**
	 * C端-获取当前用户未读消息数
	 * @return
	 */
	int getUnreadCount();

	/**
	 * 获取所有正常状态(在职/未封禁)的用户ID
	 */
	List<UserSelectVO> getActiveUserSelect();
}
