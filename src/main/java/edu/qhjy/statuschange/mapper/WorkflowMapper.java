package edu.qhjy.statuschange.mapper;

import edu.qhjy.statuschange.domain.AuditFlow;
import edu.qhjy.statuschange.domain.AuditFlowDetail;
import edu.qhjy.statuschange.domain.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkflowMapper {

    AuditFlowDetail findFlowDetailByFlowIdAndStepOrder(@Param("flowId") Long flowId, @Param("stepOrder") int stepOrder);

    AuditFlowDetail findFlowDetailByStateName(@Param("flowId") Long flowId, @Param("stateName") String stateName);

    AuditLog findLatestLog(@Param("businessKey") Long businessKey);

    void insertLog(AuditLog log);

    String findDzmByDm(String dm);

    AuditFlow findFlowByDatabaseNamAndTableName(String databaseName, String tableName);

    /**
     * 根据流程ID，查询该流程定义的最高审核步骤（即最终审核步骤）
     * @param flowId 流程ID
     * @return 最高审核步骤的详情
     */
    AuditFlowDetail findFinalStepOfFlow(@Param("flowId") Long flowId);
}