package edu.qhjy.statuschange.service.impl;

import edu.qhjy.aop.UserContext;
import edu.qhjy.statuschange.domain.AuditFlow;
import edu.qhjy.statuschange.domain.AuditFlowDetail;
import edu.qhjy.statuschange.domain.AuditLog;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.StudentLocationDTO;
import edu.qhjy.statuschange.dto.audit.WorkflowResultDTO;
import edu.qhjy.statuschange.mapper.KsxxMapper;
import edu.qhjy.statuschange.mapper.WorkflowMapper;
import edu.qhjy.statuschange.service.IWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements IWorkflowService {

    private final WorkflowMapper workflowMapper;
    private final KsxxMapper ksxxMapper;

    @Override
    @Transactional
    public WorkflowResultDTO processAudit(Long businessKey, String tableName, String applicantIdentifier, AuditRequestDTO dto, UserContext.UserInfo currentUser) {

        AuditFlow flow = workflowMapper.findFlowByTableName(tableName);
        if (flow == null) throw new IllegalArgumentException("未配置的审核业务: " + tableName);

        AuditLog lastLog = workflowMapper.findLatestLog(businessKey);
        int nextStepOrder = 1;
        if (lastLog != null) {
            if (!"通过".equals(lastLog.getAuditStatus())) {
                throw new IllegalStateException("该申请已被驳回或流程异常，无法继续审核");
            }
            AuditFlowDetail lastStepDetail = workflowMapper.findFlowDetailByStateName(flow.getAuditFlowID(), lastLog.getAuditState());
            nextStepOrder = lastStepDetail.getAuditFlowDe1() + 1;
        }

        AuditFlowDetail nextStep = workflowMapper.findFlowDetailByFlowIdAndStepOrder(flow.getAuditFlowID(), nextStepOrder);
        if (nextStep == null) {
            throw new IllegalStateException("审核流程已结束，无需再次审核");
        }

        checkPermissions(currentUser, nextStep.getAuditGroupI(), applicantIdentifier);

        AuditLog newLog = new AuditLog();
        newLog.setAuditID(businessKey);
        newLog.setAuditState(nextStep.getAuditFlowDe2());
        newLog.setAuditStatus(dto.getDecision());
        newLog.setAuditCommen(dto.getComments());
        newLog.setAuditer(currentUser.getRealName());
        newLog.setAuditDate(LocalDate.now());
        workflowMapper.insertLog(newLog);

        if ("驳回".equals(dto.getDecision())) {
            return new WorkflowResultDTO(nextStep.getAuditFlowDe2(), "驳回");
        } else if ("通过".equals(dto.getDecision())) {
            AuditFlowDetail finalStep = workflowMapper.findFinalStepOfFlow(flow.getAuditFlowID());
            int userLevel = Integer.parseInt(currentUser.getJs());
            int finalLevel = Integer.parseInt(finalStep.getAuditGroupI());
            AuditFlowDetail stepAfterNext = workflowMapper.findFlowDetailByFlowIdAndStepOrder(flow.getAuditFlowID(), nextStepOrder + 1);

            if (userLevel >= finalLevel || stepAfterNext == null) {
                return new WorkflowResultDTO(finalStep.getAuditFlowDe2(), "通过");
            } else {
                return new WorkflowResultDTO(stepAfterNext.getAuditFlowDe2(), null);
            }
        } else {
            throw new IllegalArgumentException("无效的审核决定: " + dto.getDecision());
        }
    }

    private void checkPermissions(UserContext.UserInfo currentUser, String requiredJs, String applicantIdentifier) {
        // 角色校验，使用 js 字段
        int userLevel = Integer.parseInt(currentUser.getJs());
        int requiredLevel = Integer.parseInt(requiredJs);
        if (userLevel < requiredLevel) {
            throw new IllegalStateException("您的权限级别不足，无法执行该审核操作");
        }

        //  智能的精细化区域校验
        String applicantSchoolCode;
        if (applicantIdentifier.length() == 5) {
            applicantSchoolCode = applicantIdentifier;
        } else {
            StudentLocationDTO studentLocation = ksxxMapper.findStudentLocationByKsh(applicantIdentifier);
            if (studentLocation == null)
                throw new IllegalStateException("无法定位申请学生 " + applicantIdentifier + " 的所属区划");
            applicantSchoolCode = studentLocation.getSchoolCode();
        }

        if (applicantSchoolCode == null) {
            throw new IllegalStateException("无法定位申请学生 " + applicantIdentifier + " 的所属学校");
        }

        boolean hasRegionPermission = false;
        switch (currentUser.getJs()) {
            case "1", "2", "3", "4", "5", "6", "7":
                if (applicantSchoolCode.startsWith(currentUser.getDm())) {
                    hasRegionPermission = true;
                }
                break;
            case "8":
                hasRegionPermission = true;
                break;
            default:
                break;
        }

        if (!hasRegionPermission) {
            throw new IllegalStateException("您无权审核该申请，可能是因为申请不属于您的管理区划");
        }
    }
}