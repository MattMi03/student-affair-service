package edu.qhjy.punchin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LeaveApplicationAuditDTO {
    @NotBlank(message = "审核决定不能为空")
    @Pattern(regexp = "通过|驳回", message = "审核决定只能是“通过”或“驳回”")
    private String shzt;

    private String shyj;
}