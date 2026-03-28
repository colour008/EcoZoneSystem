package com.zone.domain.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 工单处理反馈DTO - 用于B端
 */
@Data
public class WorkOrderProcessDTO implements Serializable {

    /** 工单ID */
    private Long id;

    /** * 处理反馈/备注
     * 对应表中的 remark 字段，由工人填写
     */
    private String remark;

    /** * 执行工人ID
     * 对应表中的 worker_id，标记是谁完成了这个活
     */
    private Long workerId;

    /** * 处理后的附件图片URL (留证)
     * 对应表中的 images 字段（或你之后可能新增的 finish_images）
     */
    private String images;
}