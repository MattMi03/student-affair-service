package edu.qhjy.statuschange.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditLog {
    private Long auditLogID;
    private Long auditID;      // 业务主键
    private String auditState;   // 审核阶段名称
    private String auditStatus;  // 审核状态 (APPROVED/REJECTED)
    private String auditCommen;  // 审核意见
    private String auditer;      // 审核人
    private LocalDate auditDate;    // 审核日期
}