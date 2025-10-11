package edu.qhjy.statuschange.dto.remoteclass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class YdjbDTO {
    @NotBlank(message = "学校代码不能为空")
    private String xxdm;
    @NotNull(message = "年级不能为空")
    private Integer jb;
    private Integer rs;
    private String kjzcReason;
    private String attachment;
}