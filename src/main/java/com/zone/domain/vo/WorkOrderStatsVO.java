package com.zone.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author JamHoo
 * @date 2023/3/23
 * @description 工单数量统计VO
 */
@Data
@Schema(description = "工单数量统计VO")
public class WorkOrderStatsVO {
    @Schema(description = "待受理数量")
    private Long pendingCount = 0L;

    @Schema(description = "处理中数量")
    private Long processingCount = 0L;

    @Schema(description = "已办结数量")
    private Long completedCount = 0L;

    @Schema(description = "已评价数量")
    private Long evaluatedCount = 0L;
}