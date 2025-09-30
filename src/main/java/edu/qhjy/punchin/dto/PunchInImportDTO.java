package edu.qhjy.punchin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用于JSON批量导入打卡记录的DTO
 * (前端解析Excel后，将每一行构造成此对象)
 */
@Data
public class PunchInImportDTO {
    @NotBlank(message = "考籍号不能为空")
    private String ksh;

    @NotNull(message = "打卡日期不能为空")
    private LocalDate dkrq;

    @NotNull(message = "打卡时间不能为空")
    private LocalDateTime dksj;

    private String dkdd;
    private String dksb;
}