package com.zone.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: JamHoo
 * @Description: 留言提交入参DTO
 * @Date: 2026-04-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquirySubmitDTO {
    @NotBlank(message = "姓名不能为空")
    private String applicantName;

    private String companyName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    @NotNull(message = "请选择意向主题")
    private Integer type;

    @NotBlank(message = "留言内容不能为空")
    private String remark;
}