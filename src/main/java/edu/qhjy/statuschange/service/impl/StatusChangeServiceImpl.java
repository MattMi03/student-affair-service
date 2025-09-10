package edu.qhjy.statuschange.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.statuschange.domain.*;
import edu.qhjy.statuschange.dto.*;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import edu.qhjy.statuschange.service.StatusChangeService;
import edu.qhjy.statuschange.vo.*;
import edu.qhjy.student.dto.registeration.RegistrationInfoDTO;
import edu.qhjy.student.dto.registeration.StudentInfoDTO;
import edu.qhjy.student.service.StudentRegistrationService;
import edu.qhjy.statuschange.vo.InformationChangeSummaryVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class StatusChangeServiceImpl implements StatusChangeService {

    private final StatusChangeMapper statusChangeMapper;
    private final StudentRegistrationService studentRegistrationService;

    @Override
    public StudentBasicInfoVO getBasicInfo(String ksh) {

        // 调用Mapper方法获取学生基本信息
        StudentBasicInfoVO studentInfo = statusChangeMapper.getBasicInfoByKsh(ksh);

        // 如果没有找到学生信息，返回null或抛出异常
        if (studentInfo == null) {
            return null; // 或者可以抛出自定义异常
        }

        return studentInfo;
    }

    //    新生补录相关实现
    @Override
    @Transactional
    public void createLateRegistration(LateRegistrationApplyDTO lateRegistrationApplyDTO) {
        // 创建 RegistrationInfoDTO
        RegistrationInfoDTO registrationInfo = new RegistrationInfoDTO();

        // 创建 StudentInfoDTO，并从 DTO 中拷贝属性
        StudentInfoDTO studentInfo = new StudentInfoDTO();
        BeanUtils.copyProperties(lateRegistrationApplyDTO, studentInfo);

        registrationInfo.setStudentInfo(studentInfo);

        // 校验考生号
        if (studentInfo.getKsh() == null || studentInfo.getKsh().isEmpty()) {
            throw new IllegalArgumentException("考生信息或考生号不能为空");
        }

        // 拷贝到 BasicInfoDTO
        StudentBasicInfoDTO studentBasicInfoDTO = new StudentBasicInfoDTO();
        BeanUtils.copyProperties(studentInfo, studentBasicInfoDTO);

        // 插入考籍异动记录
        Long kjydjlbs = insertKjydjl(studentBasicInfoDTO, 6L);
        registrationInfo.getStudentInfo().setKjydjlbs(kjydjlbs);

        System.out.println("Late Registration Info: " + registrationInfo);

        // 走后续逻辑
        studentRegistrationService.createRegistrationByAdmin(registrationInfo);
    }

    @Override
    public PageInfo<LateRegistrationListVO> listLateRegistrations(CommonQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<LateRegistrationListVO> list = statusChangeMapper.selectLateRegistrationList(queryDTO);
        return new PageInfo<>(list);
    }

    //    休学相关实现
    @Override
    public PageInfo<LeaveAuditListVO> getSuspension(CommonQueryDTO commonQueryDTO) {
        // 使用PageHelper进行分页查询
        PageHelper.startPage(commonQueryDTO.getPageNum(), commonQueryDTO.getPageSize());

        // 调用Mapper方法获取请假审核列表
        List<LeaveAuditListVO> leaveAuditList = statusChangeMapper.getSuspension(commonQueryDTO);

        // 返回分页信息
        return new PageInfo<>(leaveAuditList);
    }

    @Override
    @Transactional
    public void createSuspension(LeaveApplyDetailDTO leaveApplyDetailDTO) {

        Long kjydlxbs = insertKjydjl(leaveApplyDetailDTO.getStudentBasicInfoDTO(), 1L);

        Xfxjl xfxjl = new Xfxjl();
        BeanUtils.copyProperties(leaveApplyDetailDTO, xfxjl);
        xfxjl.setKjydjlbs(kjydlxbs);
        xfxjl.setXfxbj((byte) 0);
        statusChangeMapper.insertXfxjl(xfxjl);
    }

    //    复学相关实现
    @Override
    @Transactional
    public void applyForReturn(ReturnApplyDTO applyDTO) {
        // 1. 创建总表记录
        Long kjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 2L);
        Xfxjl xiuxue = statusChangeMapper.getLatestXfxjlByKjydjlbs(applyDTO.getStudentBasicInfoDTO().getKsh());

        if (xiuxue == null || xiuxue.getXfxbj() != 0) {
            throw new IllegalArgumentException("该学生没有未复学的休学记录，无法申请复学");
        }

        // 2. 创建 xfxjl 分表记录，并设置复学标记
        Xfxjl xfxjl = new Xfxjl();
        BeanUtils.copyProperties(applyDTO, xfxjl);
        xfxjl.setKjydjlbs(kjydjlbs);
        xfxjl.setXfxbj((byte) 1); // 关键：设置标记为 1 (复学)
        xfxjl.setJdnj(xiuxue.getJdnj());
        xfxjl.setXxmc(xiuxue.getXxmc());
        xfxjl.setXxrq(xiuxue.getXxrq());
        xfxjl.setXxyy(xiuxue.getXxyy());
        xfxjl.setXxsc(xiuxue.getXxsc());

        statusChangeMapper.insertXfxjl(xfxjl);
    }

    @Override
    public PageInfo<ReturnAuditListVO> listReturnApplications(CommonQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<ReturnAuditListVO> list = statusChangeMapper.selectReturnList(queryDTO);
        return new PageInfo<>(list);
    }

    //   转学相关实现
    @Override
    public PageInfo<TransferAuditListVO> getTransfer(CommonQueryDTO commonQueryDTO, Long zxlx) {
        // 使用PageHelper进行分页查询
        PageHelper.startPage(commonQueryDTO.getPageNum(), commonQueryDTO.getPageSize());

        // 调用Mapper方法获取转学审核列表
        List<TransferAuditListVO> transferAuditList = statusChangeMapper.getTransfer(commonQueryDTO, zxlx);

        // 返回分页信息
        return new PageInfo<>(transferAuditList);
    }

    @Override
    @Transactional
    public void applyTransfer(TransferApplyDetailDTO applyDTO, Long zxlx) {
        // 1. 为主申请创建总记录和详情记录
        Long mainKjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 4L);

        Zxjl mainZxjl = new Zxjl();
        BeanUtils.copyProperties(applyDTO, mainZxjl);
        mainZxjl.setSzqmc(applyDTO.getZrsfmc());
        mainZxjl.setSzsmc(applyDTO.getZrcsmc());
        mainZxjl.setSzxmc(applyDTO.getZrqxmc());
        mainZxjl.setKjydjlbs(mainKjydjlbs);
        mainZxjl.setZxlx(zxlx);
        statusChangeMapper.insertZxjl(mainZxjl);

        // 2. 【核心逻辑】根据主申请类型，双向创建关联的申请记录
        if (zxlx == 1) {
            // 主申请是“省内转出”，同步创建“省内转入”
            createLinkedTransferRecord(applyDTO, 2L);
        } else if (zxlx == 2) {
            // 主申请是“省内转入”，同步创建“省内转出”
            createLinkedTransferRecord(applyDTO, 1L);
        }
    }

    /**
     * 创建一条关联的转学记录（总表+分表）
     *
     * @param applyDTO   原始的申请数据
     * @param linkedZxlx 要创建的关联记录的子类型 (1或2)
     */
    private void createLinkedTransferRecord(TransferApplyDetailDTO applyDTO, Long linkedZxlx) {
        // 为关联申请创建一条全新的、独立的 kjydjl 总记录
        Long linkedKjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 4L);

        // 创建一个新的 Zxjl 对象来存储关联申请的详情
        Zxjl linkedZxjl = new Zxjl();
        BeanUtils.copyProperties(applyDTO, linkedZxjl); // 复用相同的转学信息
        linkedZxjl.setKjydjlbs(linkedKjydjlbs);      // 关联到新的总记录ID
        linkedZxjl.setZxlx(linkedZxlx);              // 设置关联记录的子类型

        statusChangeMapper.insertZxjl(linkedZxjl);
    }

    @Override
    public PageInfo<AttritionListVO> listAttritionApplications(CommonQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AttritionListVO> list = statusChangeMapper.selectAttritionList(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void applyForAttrition(AttritionApplyDTO applyDTO) {
        // 1. 调用复用的方法创建总表记录
        // 假设“流失”在 kjydlx 表中的ID为 3L
        Long kjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 5L);

        // 2. 创建并插入 lsjl (流失分表) 记录
        Lsjl lsjl = new Lsjl();
        lsjl.setKjydjlbs(kjydjlbs);
        lsjl.setLsyy(applyDTO.getLsyy());
        lsjl.setLssj(applyDTO.getLssj());

        statusChangeMapper.insertLsjl(lsjl);
    }

    private Long insertKjydjl(StudentBasicInfoDTO studentBasicInfoDTO, Long kjydlxbs) {
        Kjydjl kjydjl = new Kjydjl();
        BeanUtils.copyProperties(studentBasicInfoDTO, kjydjl);
        kjydjl.setKjydlxbs(kjydlxbs);
        kjydjl.setShzt("待审核");
        kjydjl.setShjd("学校审核");
        statusChangeMapper.insertKjydjl(kjydjl);
        return kjydjl.getKjydjlbs();
    }

    // 出国相关实现
    @Override
    @Transactional
    public void applyForAbroad(AbroadApplyDTO applyDTO) {
        // 1. 创建考籍异动记录
        Long kjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 7L);
        // 2. 创建出国登记记录
        Cgdj cgdj = new Cgdj();
        BeanUtils.copyProperties(applyDTO, cgdj);
        cgdj.setKjydjlbs(kjydjlbs);
        statusChangeMapper.insertAbroadRegistration(cgdj);
    }

    @Override
    @Transactional
    public void applyForKeyPropertyChange(KeyPropertyChangeApplyDTO applyDTO) {
        // 1. 创建总表记录
        // 假设“关键属性修改”在 kjydlx 表中的ID为 6L
        Long kjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 3L);

        // 2. 创建并插入  (详情分表) 记录
        Xxbgjl detail = new Xxbgjl();
        detail.setKjydjlbs(kjydjlbs);
        detail.setGgsxmc(applyDTO.getGgsxmc());
        detail.setGqz(applyDTO.getGqz());
        detail.setGhz(applyDTO.getGhz());

        statusChangeMapper.insertKeyPropertyChange(detail);
    }

    @Override
    public PageInfo<KeyPropertyChangeListVO> listKeyPropertyChangeApps(CommonQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<KeyPropertyChangeListVO> list = statusChangeMapper.selectKeyPropertyChangeList(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<AbroadListVO> listAbroadApplications(CommonQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AbroadListVO> abroadList = statusChangeMapper.selectAbroadList(queryDTO);
        return new PageInfo<>(abroadList);
    }

    @Override
    @Transactional
    public String deleteStatusChangeRecord(Long kjydjlbs) {
        // 1. 先从总表查询出这条记录的类型
        Long recordType = statusChangeMapper.getKjydlxbsByKjydjlbs(kjydjlbs);
        String type = "考籍异动";
        // 2. 根据记录类型，精确地删除对应的分表记录
        if (recordType == 1L) { // 1L = 休学
            statusChangeMapper.deleteXfxjlByKjydjlbs(kjydjlbs);
            type = "休学";
        } else if (recordType == 2L) { // 2L = 复学
            statusChangeMapper.deleteXfxjlByKjydjlbs(kjydjlbs);
            type = "复学";
        } else if (recordType == 3L) { // 3L = 关键属性修改
            statusChangeMapper.deleteXxbgjlByKjydjlbs(kjydjlbs);
            type = "信息变更";
        } else if (recordType == 4L) { // 4L = 转学
            statusChangeMapper.deleteZxjlByKjydjlbs(kjydjlbs);
            type = "转学";
        } else if (recordType == 5L) { // 5L = 流失
            statusChangeMapper.deleteLsjlByKjydjlbs(kjydjlbs);
            type = "流失";
        } else if (recordType == 7L) { // 7L = 出国登记
            statusChangeMapper.deleteXxbgjlByKjydjlbs(kjydjlbs);
            type = "出国登记";
        }

        // 3. 最后，删除总表记录
        statusChangeMapper.deleteKjydjlById(kjydjlbs);
        return type;
    }

    // 统计相关实现
    @Override
    public PageInfo<InformationChangeSummaryVO> listInformationChangeSummary(CommonQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<InformationChangeSummaryVO> list = statusChangeMapper.selectInformationChangeSummary(queryDTO);
        return new PageInfo<>(list);
    }
}
