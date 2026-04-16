package com.zone.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 意向留言跟进明细实体 - 对应 biz_inquiry_record
 */
@Data
public class InquiryRecord {
    private Long id;
    private Long inquiryId;    // 关联主表ID
    private Integer actionType; // 动作: 1分配 2跟进反馈 3转入驻 4关闭
    private String content;    // 本次跟进的具体内容
    private Long handlerId;    // 跟进人ID
    private String handlerName; // 跟进人姓名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 跟进时间
}