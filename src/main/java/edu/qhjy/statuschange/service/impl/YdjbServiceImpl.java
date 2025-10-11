package edu.qhjy.statuschange.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.statuschange.domain.Ydjb;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.WorkflowResultDTO;
import edu.qhjy.statuschange.dto.remoteclass.AssignStudentsDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbQueryDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbStudentQueryDTO;
import edu.qhjy.statuschange.mapper.YdjbMapper;
import edu.qhjy.statuschange.service.IWorkflowService;
import edu.qhjy.statuschange.service.YdjbService;
import edu.qhjy.statuschange.vo.remoteclass.YdjbListVO;
import edu.qhjy.statuschange.vo.remoteclass.YdjbStudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class YdjbServiceImpl implements YdjbService {

    private final YdjbMapper ydjbMapper;
    private final IWorkflowService workflowService;

    @Override
    @Transactional
    public Ydjb createYdjb(YdjbDTO dto, Integer bType, String creatorName) {
        Ydjb ydjb = new Ydjb();
        BeanUtils.copyProperties(dto, ydjb);
        ydjb.setBType(bType);
        ydjb.setCjrxm(creatorName);
        ydjb.setCjsj(LocalDateTime.now());
        ydjbMapper.insert(ydjb);
        return ydjb;
    }

    @Override
    @Transactional
    public void deleteYdjb(Long ydjbbs) {
        // TODO: 增加校验，如果此申请下已有关联学生，则禁止删除
        if (ydjbMapper.findById(ydjbbs) == null) {
            throw new NoSuchElementException("集体办班记录记录不存在");
        }
        ydjbMapper.clearStudentsFromYdjb(ydjbbs);
        ydjbMapper.deleteById(ydjbbs);
    }

    @Override
    @Transactional
    public void updateYdjb(Long ydjbbs, YdjbDTO dto) {
        Ydjb existing = ydjbMapper.findById(ydjbbs);
        if (existing == null) {
            throw new NoSuchElementException("集体办班记录记录不存在");
        }
        // TODO: 增加校验，如果已审核，则禁止修改
        BeanUtils.copyProperties(dto, existing);
        ydjbMapper.update(existing);
    }

    @Override
    public Ydjb getYdjbById(Long ydjbbs) {
        Ydjb ydjb = ydjbMapper.findById(ydjbbs);
        if (ydjb == null) {
            throw new NoSuchElementException("ID为 " + ydjbbs + " 的记录不存在");
        }
        return ydjb;
    }

    @Override
    public PageInfo<YdjbListVO> listYdjb(YdjbQueryDTO query, int pageNum, int pageSize) {
        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            query.setPermissionDm(user.getDm());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<YdjbListVO> list = ydjbMapper.findForPage(query);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void deleteYdjbBatch(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            // TODO: 增加校验，如果任何一个申请下已有关联学生，则禁止删除
            ydjbMapper.deleteBatchByIds(ids);
        }
    }

    @Override
    public PageInfo<YdjbStudentVO> listStudents(Long ydjbbs, YdjbStudentQueryDTO query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<YdjbStudentVO> list = ydjbMapper.findStudentsByYdjbbs(ydjbbs, query);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void addStudents(Long ydjbbs, AssignStudentsDTO dto) {
        // TODO: 校验学生是否存在，是否符合加入条件
        if (CollectionUtils.isEmpty(dto.getKsbsList())) {
            throw new IllegalArgumentException("未提供任何学生");
        }
        if (ydjbMapper.findById(ydjbbs) == null) {
            throw new NoSuchElementException("集体办班记录记录不存在");
        }
        ydjbMapper.addStudentsToYdjb(ydjbbs, dto.getKsbsList());
    }

    @Override
    @Transactional
    public void removeStudents(Long ydjbbs, AssignStudentsDTO dto) {
        ydjbMapper.removeStudentsFromYdjb(ydjbbs, dto.getKsbsList());
    }

    @Override
    @Transactional
    public void auditYdjb(Long ydjbbs, AuditRequestDTO dto) {
        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser == null) {
            throw new IllegalStateException("请登录后再进行操作");
        }
        Ydjb application = ydjbMapper.findById(ydjbbs);
        if (application == null) {
            throw new NoSuchElementException("找不到ID为 " + ydjbbs + " 的申请记录");
        }
        if (!"待审核".equals(application.getShzt())) {
            throw new IllegalStateException("该申请已被处理，无法重复审核");
        }

        // 1. 根据办班类型 (bType) 确定业务表名
        String tableName = switch (application.getBType()) {
            case 1 -> "YDJB-SW"; // 省外
            case 2 -> "YDJB-SN"; // 省内
            default -> throw new IllegalStateException("未知的办班类型: " + application.getBType());
        };

        // 2. 【核心】调用通用工作流服务
        // 注意：我们将申请单的 "xxdm" 作为 applicantIdentifier 传递
        WorkflowResultDTO result = workflowService.processAudit(
                ydjbbs,
                tableName,
                application.getXxdm(), // 使用学校代码进行权限校验
                dto,
                currentUser
        );

        // 3. 根据工作流返回的结果，更新业务表状态
        if (result.getFinalStatus() != null) {
            application.setShzt(result.getFinalStatus());
        } else {
            application.setShzt("待审核"); // 流程继续
        }

        application.setShjd(result.getStageName());
        application.setShrxm(currentUser.getRealName()); // 记录最新审核人姓名
        application.setShsj(LocalDateTime.now()); // 记录最新审核时间
        application.setShyj(dto.getComments()); // 记录最新审核意见

        ydjbMapper.update(application);
    }
}