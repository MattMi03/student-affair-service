package edu.qhjy.statuschange.domain;

import lombok.Data;

@Data
public class AuditFlowDetail {
    private Long auditFlowDe;
    private Long auditFlowID;
    private String auditGroupI; // 审核组/角色ID
    private Integer auditFlowDe1; // 步骤顺序
    private String auditFlowDe2; // 步骤名称
}