package edu.qhjy.statuschange.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AttritionApplyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 内嵌的学生基本信息
     */
    @NotNull
    @Valid
    private StudentBasicInfoDTO studentBasicInfoDTO;

    /**
     * 流失原因
     */
    @NotBlank(message = "流失原因不能为空")
    private String lsyy;

    /**
     * 流失时间
     */
    @NotNull(message = "流失时间不能为空")
    private LocalDate lssj;
}