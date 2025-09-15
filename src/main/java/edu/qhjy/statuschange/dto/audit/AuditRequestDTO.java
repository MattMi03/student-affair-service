package edu.qhjy.statuschange.dto.audit;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuditRequestDTO {
    @NotBlank(message = "审核决定不能为空")
    private String decision; // "APPROVED" 或 "REJECTED"

    private String comments; // 审批意见
}