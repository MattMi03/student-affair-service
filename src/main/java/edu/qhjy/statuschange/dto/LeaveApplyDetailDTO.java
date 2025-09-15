package edu.qhjy.statuschange.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class LeaveApplyDetailDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "申请休学原因不能为空")
    private String xxyy;

    @NotNull(message = "休学日期不能为空")
    private LocalDate xxrq;

    @NotBlank(message = "就读年级不能为空")
    private String jdnj;

    @NotBlank(message = "学校名称不能为空")
    private String xxmc;

    @NotBlank(message = "医院证明文件不能为空")
    private String yyzmwjdz;

    private String xxsc = "一年";

    @NotNull
    @Valid
    private StudentBasicInfoDTO studentBasicInfoDTO;
}