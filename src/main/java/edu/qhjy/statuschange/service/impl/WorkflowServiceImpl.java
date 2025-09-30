package edu.qhjy.statuschange.service.impl;


import edu.qhjy.statuschange.domain.AuditFlow;
import edu.qhjy.statuschange.domain.AuditFlowDetail;
import edu.qhjy.statuschange.domain.AuditLog;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.StudentLocationDTO;
import edu.qhjy.statuschange.dto.audit.UserInfo;
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
    private final KsxxMapper ksxxMapper; // 用于获取学生区划信息以进行精细化权限校验

    public static String getRoleName(String roleId) {
        if (roleId == null) return "未知角色";
        switch (roleId) {
            case "0":
                return "考生";
            case "1":
                return "班主任";
            case "2":
                return "学校管理员";
            case "3":
                return "区县招办";
            case "4":
                return "考区管理员";
            case "5":
                return "市招考办";
            case "6":
                return "市州学考管理员";
            case "7":
                return "学考处";
            case "8":
                return "省考办";
            default:
                return "未知角色";
        }
    }

    @Override
    @Transactional
    public WorkflowResultDTO processAudit(Long businessKey, String tableName, String applicantKsh, AuditRequestDTO dto, UserInfo currentUser) {

        currentUser.setDzm(workflowMapper.findDzmByDm(currentUser.getDm()));
        // 1. 获取流程定义
        AuditFlow flow = workflowMapper.findFlowByTableName(tableName);
        if (flow == null) throw new IllegalArgumentException("未配置的审核业务: " + tableName);

        // 2. 查找最新日志，确定当前进行到哪一步
        AuditLog lastLog = workflowMapper.findLatestLog(businessKey);

        int nextStepOrder = 1; // 默认从第一步开始
        if (lastLog != null) {
            if (!"通过".equals(lastLog.getAuditStatus())) {
                throw new IllegalStateException("该申请已被驳回或流程异常，无法继续审核");
            }
            // 通过日志中的阶段名称反查步骤顺序
            AuditFlowDetail lastStepDetail = workflowMapper.findFlowDetailByStateName(flow.getAuditFlowID(), lastLog.getAuditState());
            nextStepOrder = lastStepDetail.getAuditFlowDe1() + 1;
        }

        // 3. 获取下一步的审核要求
        AuditFlowDetail nextStep = workflowMapper.findFlowDetailByFlowIdAndStepOrder(flow.getAuditFlowID(), nextStepOrder);
        if (nextStep == null) {
            throw new IllegalStateException("审核流程已结束，无需再次审核");
        }

        // 4. 权限校验 (角色 + 精细化区域校验)
        checkPermissions(currentUser, nextStep.getAuditGroupI(), applicantKsh);

        // 5. 记录新的审核日志
        AuditLog newLog = new AuditLog();
        newLog.setAuditID(businessKey);
        newLog.setAuditState(nextStep.getAuditFlowDe2()); // 记录当前完成的阶段名称
        newLog.setAuditStatus(dto.getDecision());
        newLog.setAuditCommen(dto.getComments());
        newLog.setAuditer(currentUser.getName());
        newLog.setAuditDate(LocalDate.now());
        workflowMapper.insertLog(newLog);

        // 步骤 6. 计算并返回下一步的状态或最终结果
        if ("驳回".equals(dto.getDecision())) {
            return new WorkflowResultDTO(nextStep.getAuditFlowDe2(), "驳回");
        } else { // 审核通过

            // 查找最终步骤
            AuditFlowDetail finalStep = workflowMapper.findFinalStepOfFlow(flow.getAuditFlowID());
            int userLevel = Integer.parseInt(currentUser.getGroupId());
            int finalLevel = Integer.parseInt(finalStep.getAuditGroupI());

            // 查找再下一步
            AuditFlowDetail stepAfterNext = workflowMapper.findFlowDetailByFlowIdAndStepOrder(flow.getAuditFlowID(), nextStepOrder + 1);

            // 如果当前用户的级别 >= 整个流程的最高要求级别，或者没有更下一步了，则流程结束, 设置阶段为最终阶段，状态为通过。
            if (userLevel >= finalLevel || stepAfterNext == null) {
                return new WorkflowResultDTO(finalStep.getAuditFlowDe2(), "通过");
            } else {
                // 如果还有更下一步，正常流转。
                // 阶段设置为下一步的阶段，最终状态为null表示流程继续。
                return new WorkflowResultDTO(stepAfterNext.getAuditFlowDe2(), null);
            }
        }
    }

    private void checkPermissions(UserInfo currentUser, String requiredGroupId, String applicantKsh) {
        // 3.1 角色校验
        int userLevel = Integer.parseInt(currentUser.getGroupId());
        int requiredLevel = Integer.parseInt(requiredGroupId);

        if (userLevel < requiredLevel) {
            // 如果用户的级别小于当前步骤要求的最低级别，则无权限
            throw new IllegalStateException("您的权限级别 (" + getRoleName(currentUser.getGroupId()) + ") 低于当前操作所需的最低级别 (" + getRoleName(requiredGroupId) + ")");
        }

        // 3.2 精细化区域校验
        StudentLocationDTO studentLocation = ksxxMapper.findStudentLocationByKsh(applicantKsh);
        if (studentLocation == null) throw new IllegalStateException("无法定位申请学生的所属区划");

        boolean hasRegionPermission = false;
        switch (currentUser.getGroupId()) {
            case "1": // 班主任
            case "2": // 学校管理员
            case "3": // 区县招办
            case "4": // 考区管理员
            case "5": // 市招考办
            case "6": // 市州学考管理员
            case "7": // 学考处
                hasRegionPermission = studentLocation.getSchoolCode().startsWith(currentUser.getDm());
                break;
            case "8": // 省考办
                hasRegionPermission = true; // 省级默认有权限
                break;
            default:
                break;
        }

        if (!hasRegionPermission) {
            throw new IllegalStateException("您无权审核该学生的申请，可能是因为学生不属于您的管理区划");
        }
    }
}