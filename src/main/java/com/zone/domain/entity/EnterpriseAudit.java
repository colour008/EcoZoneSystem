package com.zone.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 企业审核流水实体类 - 对应数据库 biz_enterprise_audit
 * @Date: 2026/3/22 13:12
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseAudit {

	private Long id; // 主键ID

	private Long enterpriseId; // 关联的企业ID

	private Integer status; // 0:待审核 1:已入驻 2:驳回 3:迁出

	private String opinion; // 审核意见

	private Long auditorId; // 审核人ID

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createTime; // 创建时间
}
