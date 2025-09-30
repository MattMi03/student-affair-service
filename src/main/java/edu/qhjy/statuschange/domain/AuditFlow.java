package edu.qhjy.statuschange.domain;

import lombok.Data;

@Data
public class AuditFlow {
    private Long auditFlowID;
    private String auditFlowNa; // 流程名称
    private String tableName;   // 关联的业务表名
    private String kqdmList; // 适用的考区代码列表，逗号分隔
    private String auditName; // 审核名称
}