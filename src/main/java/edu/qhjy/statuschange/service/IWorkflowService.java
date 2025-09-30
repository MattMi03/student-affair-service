package edu.qhjy.statuschange.service;


import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.UserInfo;
import edu.qhjy.statuschange.dto.audit.WorkflowResultDTO;

public interface IWorkflowService {
    /**
     * 通用的审核处理方法
     *
     * @return 返回下一步的审核阶段名称；如果流程结束，则返回最终状态 ("APPROVED" 或 "REJECTED")
     */
    WorkflowResultDTO processAudit(Long businessKey, String tableName, String applicantKsh, AuditRequestDTO dto, UserInfo currentUser);
}