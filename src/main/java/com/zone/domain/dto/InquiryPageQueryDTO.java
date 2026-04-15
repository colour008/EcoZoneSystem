package com.zone.domain.dto;

import com.zone.domain.base.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: B端意向留言分页查询DTO
 * @Date: 2026-04-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryPageQueryDTO extends PageQuery {
    private String applicantName; // 申请人/联系人姓名
    private String companyName;   // 企业名称
    private String contactPhone;  // 联系电话
    private Integer type;         // 意向类型
    private Integer status;       // 处理状态
    private String handlerName;   // 跟进人姓名(用于联表模糊查询)
}