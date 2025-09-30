package edu.qhjy.rollcall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RollCallImportDTO {
    @NotBlank(message = "考生号不能为空")
    private String ksh;
    @NotBlank(message = "考勤状态不能为空")
    private String kqzt;

    private LocalDateTime kqsj;
    private String zqry;
}