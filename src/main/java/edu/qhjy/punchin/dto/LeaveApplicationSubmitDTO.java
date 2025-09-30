package edu.qhjy.punchin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveApplicationSubmitDTO {
    private String ksh;
    @NotNull(message = "开始日期不能为空")
    private LocalDate ksrq;
    @NotNull(message = "结束日期不能为空")
    private LocalDate jsrq;
    private String zmcllb;
}