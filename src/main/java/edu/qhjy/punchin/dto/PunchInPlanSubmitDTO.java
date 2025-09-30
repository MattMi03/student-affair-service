package edu.qhjy.punchin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PunchInPlanSubmitDTO {
    private String xnmc;

    @NotNull(message = "学期开始日期不能为空")
    private LocalDate xqksrq;

    @NotNull(message = "学期截止日期不能为空")
    private LocalDate xqjzrq;

    @NotBlank(message = "学校代码不能为空")
    private String xxdm; // 学校代码 (对应XXDM)

    @NotNull
    private List<LocalDate> punchDates; // 具体的打卡日期列表
}