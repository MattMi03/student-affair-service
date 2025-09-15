package edu.qhjy.statuschange.domain;

import lombok.Data;

@Data
public class AuditFlow {
    private Long auditFlowID;
    private String auditFlowNa; // 流程名称
    private String databaseName; // 关联的数据库名
    private String tableName;   // 关联的业务表名
    private String fieldName;   // 关联的业务主键字段名
}