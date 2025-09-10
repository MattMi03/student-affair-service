package edu.qhjy.statuschange.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LateRegistrationApplyDTO {
    @NotBlank private String ksh;    // 考籍号（必须）
    @NotBlank private String xm;     // 姓名（必须）
    @NotBlank private String xb;     // 性别（必须）
    @NotBlank private String mz;     // 民族（必须）
    @NotBlank private String sfzjh;  // 身份证号（必须）
    @NotNull  private Long bjbs;     // 现就读班级标识（
    private String xxmc;     // 学校名（可选）
}