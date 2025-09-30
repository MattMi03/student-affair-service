package edu.qhjy.statuschange.dto.audit;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员为复学申请补充新年级、新班级信息的 DTO
 */
@Data
public class TransUpdateDTO {

    @NotBlank(message = "新班级不能为空")
    private String xbjmc; // 新班级名称

    //    @NotNull(message = "新班级不能为空")
    private Long xbjbs;

}