package edu.qhjy.statuschange.dto.audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuditRequestDTO {
    @NotBlank(message = "审核决定不能为空")
    @Pattern(regexp = "通过|驳回", message = "审核决定不合法")
    private String decision; // 审核决定 (通过/驳回)

    private String comments; // 审批意见
}