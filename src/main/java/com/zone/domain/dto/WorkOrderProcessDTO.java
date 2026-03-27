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
     * 对应表中的 remark 字段
     */
    private String remark;

    /** * 处理人ID 
     * 虽然可以从 SecurityContext 获取，但某些场景下（如代办）需要手动传入
     */
    private Long handlerId;

    /** * 处理后的附件图片URL (可选)
     * 如果处理完后需要拍照留证，可以更新此字段
     */
    private String images;
}