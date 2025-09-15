package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkflowTask {
    private Long taskID;
    private Long auditFlowID;  // 关联 auditflow, 指明是什么业务
    private Long businessKey;  // 业务主键 (例如 kjydjlbs 或 hlsqbs)
    private Integer currentStepOrder; // 当前走到了第几步 (关联 auditflowdetail.AuditFlowDe1)
    private String status;         // 任务状态 (PENDING, APPROVED, REJECTED)
    private String applicantKsh;   // 申请学生的考生号
    private Date applicationTime;  // 申请时间
}