package com.zone.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zone.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: JamHoo
 * @Description: 意向留言返回VO
 * @Date: 2026-04-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryVO extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String applicantName;
	private String companyName;
	private String contactPhone;
	private Integer type;
	private String remark;
	private Integer status;
	private String handlerName; // 关联查询出的跟进人姓名
	private String handleResult;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime handleTime;
}