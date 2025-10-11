package edu.qhjy.qzsh.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.qzsh.domain.Qzsh;
import edu.qhjy.qzsh.dto.QzshQueryDTO;
import edu.qhjy.qzsh.dto.QzshUpsertDTO;
import edu.qhjy.qzsh.mapper.QzshMapper;
import edu.qhjy.qzsh.service.QzshService;
import edu.qhjy.qzsh.vo.QzshListVO;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.WorkflowResultDTO;
import edu.qhjy.statuschange.service.IWorkflowService;
import edu.qhjy.util.cache.DictCacheUtil;
import edu.qhjy.util.constants.DictTypeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QzshServiceImpl implements QzshService {

    private final QzshMapper qzshMapper;
    private final DictCacheUtil dictCacheUtil;
    private final IWorkflowService workflowService;

    @Override
    public PageInfo<QzshListVO> listQzsh(QzshQueryDTO query) {
        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            query.setPermissionDm(user.getDm());
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<QzshListVO> list = qzshMapper.findForPage(query);
        return new PageInfo<>(list);
    }

    @Override
    public Qzsh getQzshById(Long ksbs) {
        Qzsh qzsh = qzshMapper.findById(ksbs);
        if (qzsh == null) {
            throw new NoSuchElementException("ID为 " + ksbs + " 的前置审核信息不存在");
        }
        return qzsh;
    }

    @Override
    @Transactional
    public Qzsh createOrUpdateQzsh(QzshUpsertDTO dto) {
        // 1. 校验学生是否存在
        if (qzshMapper.existsByKsbs(dto.getKsbs()) == 0) { // 假设 studentMapper 有此方法
            throw new NoSuchElementException("考生标识(ksbs) " + dto.getKsbs() + " 对应的学生不存在");
        }

        if (dto.getXblx() != null) {
            String xblxMc = dictCacheUtil.getCode(DictTypeConstants.XBLX, dto.getXblx());
            if (xblxMc == null) {
                throw new NoSuchElementException("非法请求");
            }
        }

        if (dto.getKslx() != null) {
            String kslxMc = dictCacheUtil.getCode(DictTypeConstants.KSLX, dto.getKslx());
            if (kslxMc == null) {
                throw new NoSuchElementException("非法请求");
            }
        }

        Qzsh qzsh = new Qzsh();
        BeanUtils.copyProperties(dto, qzsh);

        // 2. 判断是新增还是更新
        if (qzshMapper.existsById(dto.getKsbs()) > 0) {
            if (!"待审核".equals(qzshMapper.findById(dto.getKsbs()).getShzt())) {
                throw new IllegalStateException("只能修改待审核状态的前置审核信息");
            }
            qzsh.setShzt("待审核"); // 更新时重置为待审核
            qzshMapper.update(qzsh);
        } else {
            // 新增
            qzsh.setShzt("待审核"); // 更新时重置为待审核
            qzsh.setShjd("初审");
            qzshMapper.insert(qzsh);

            if (dto.getAuditRequestDTO().getDecision() != null) {
                // 如果前端传来了审核请求，说明用户希望在创建后立即提交审核
                audit(qzsh.getKsbs(), dto.getAuditRequestDTO());
            }
        }
        return qzshMapper.findById(dto.getKsbs());
    }

    @Override
    @Transactional
    public void deleteQzsh(Long ksbs) {
        if (qzshMapper.existsById(ksbs) == 0) {
            throw new NoSuchElementException("ID为 " + ksbs + " 的前置审核信息不存在");
        }
        qzshMapper.deleteById(ksbs);
    }

    @Override
    @Transactional
    public void deleteQzshBatch(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            qzshMapper.deleteBatchByIds(ids);
        }
    }

    @Override
    @Transactional
    public void audit(Long ksbs, AuditRequestDTO auditRequestDTO) {

        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser == null) {
            throw new IllegalStateException("未获取到当前用户信息，无法进行审核操作");
        }

        // 1. 校验业务对象是否存在且状态正确
        Qzsh application = qzshMapper.findById(ksbs);

        if (application == null) {
            throw new NoSuchElementException("找不到ID为 " + ksbs + " 的前置审核申请");
        }
        if (!"待审核".equals(application.getShzt())) {
            throw new IllegalStateException("该申请已被处理，无法重复审核");
        }

        // 2. 获取用于权限校验的申请人标识 (KSH)
        String applicantKsh = qzshMapper.findKshByKsbs(ksbs);
        if (applicantKsh == null) {
            throw new IllegalStateException("找不到考生，无法进行审核");
        }

        // 3. 定义业务表名，用于查找审核流程
        final String tableName = "QZSH";

        // 4. 【核心】调用通用工作流服务
        WorkflowResultDTO result = workflowService.processAudit(
                ksbs,
                tableName,
                applicantKsh, // 使用学生的KSH进行权限校验
                auditRequestDTO,
                currentUser
        );

        // 5. 根据工作流返回的结果，更新业务表状态
        if (result.getFinalStatus() != null) {
            application.setShzt(result.getFinalStatus());
        } else {
            application.setShzt("待审核"); // 流程继续，状态不变
        }

        application.setShjd(result.getStageName());
        application.setShyj(auditRequestDTO.getComments());
        application.setShrxm(currentUser.getRealName());
        application.setShsj(java.time.LocalDate.now().toString());

        // 更新数据库
        qzshMapper.update(application);
    }
}
