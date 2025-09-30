// 文件路径: edu.qhjy.student.dto.registeration.AuditRequestDTO.java
package edu.qhjy.student.dto.registeration;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditRequestDTO {

    /**
     * 审核意见
     */
//    @NotNull(message = "审核意见不能为空")
    private String shyj; // 审核意见

    /**
     * 审核状态 (例如: "通过", "驳回")
     */
    @NotNull(message = "审核状态不能为空")
    private String shzt; // 审核状态
}