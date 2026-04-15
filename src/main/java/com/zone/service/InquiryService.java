package com.zone.service;

import com.zone.domain.base.PageResult;
import com.zone.domain.dto.InquiryPageQueryDTO;
import com.zone.domain.dto.InquirySubmitDTO;
import com.zone.domain.vo.InquiryVO;

public interface InquiryService {
    // ================== C端：公开接口 ==================

    /**
     * 提交意向留言
     * @param dto
     * @param ip
     */
    void submitInquiry(InquirySubmitDTO dto, String ip);

    // ================== B端：管理接口 ==================

    /**
     * 分页查询
     * @param dto
     * @return
     */
    PageResult<InquiryVO> getAdminPage(InquiryPageQueryDTO dto);

    /**
     * 分配跟进人
     * @param inquiryId
     * @param handlerId
     * @return
     */
    boolean assignHandler(Long inquiryId, Long handlerId);

    /**
     * 跟进记录
     * @param inquiryId
     * @param result
     * @param status
     * @return
     */
    boolean recordFollowUp(Long inquiryId, String result, Integer status);

    /**
     * 转为入驻咨询
     * @param inquiryId
     * @return
     */
    boolean convertToEnterprise(Long inquiryId);
}