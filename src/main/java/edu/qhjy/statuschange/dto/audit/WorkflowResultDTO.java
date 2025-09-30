package edu.qhjy.statuschange.dto.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流处理结果的DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResultDTO {
    /**
     * 下一个审核阶段的名称。
     * 如果流程结束，这里将是最终阶段的名称。
     */
    private String stageName;

    /**
     * 流程的最终状态。
     * 如果流程仍在进行中，则为 null。
     * 如果流程结束，则为 "通过" 或 "驳回"。
     */
    private String finalStatus;
}