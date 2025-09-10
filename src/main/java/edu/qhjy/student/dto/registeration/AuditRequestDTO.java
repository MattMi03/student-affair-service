// 文件路径: edu.qhjy.student.dto.registeration.AuditRequestDTO.java
package edu.qhjy.student.dto.registeration;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditRequestDTO {

    /**
     * 审核意见
     */
    private String shyj; // 审核意见

    /**
     * 审核状态 (例如: "审核通过", "审核驳回")
     */
    @NotNull
    private String shzt; // 审核状态
}