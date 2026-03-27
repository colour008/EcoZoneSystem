package com.zone.domain.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 工单评价DTO - 用于C端
 */
@Data
public class WorkOrderEvaluateDTO implements Serializable {

    /** 工单ID */
    private Long id;

    /** * 评分 (1-5星)
     * 对应表中的 score 字段
     */
    private Integer score;

    /** * 用户评价内容 
     * 对应表中的 comment_text 字段
     */
    private String commentText;
}