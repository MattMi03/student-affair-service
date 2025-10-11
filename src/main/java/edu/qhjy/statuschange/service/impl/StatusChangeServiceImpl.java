package edu.qhjy.statuschange.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.statuschange.domain.*;
import edu.qhjy.statuschange.dto.*;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.ResumptionUpdateDTO;
import edu.qhjy.statuschange.dto.audit.TransUpdateDTO;
import edu.qhjy.statuschange.dto.audit.WorkflowResultDTO;
import edu.qhjy.statuschange.dto.delete.KjydjlRecordDTO;
import edu.qhjy.statuschange.handler.IStatusChangeHandler;
import edu.qhjy.statuschange.mapper.StatusChangeMapper;
import edu.qhjy.statuschange.service.IWorkflowService;
import edu.qhjy.statuschange.service.StatusChangeService;
import edu.qhjy.statuschange.vo.*;
import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.dto.registeration.RegistrationInfoDTO;
import edu.qhjy.student.dto.registeration.StudentInfoDTO;
import edu.qhjy.student.mapper.ClassManagerMapper;
import edu.qhjy.student.service.StudentRegistrationService;
import edu.qhjy.util.IdCardUtils;
import edu.qhjy.util.cache.DictCacheUtil;
import edu.qhjy.util.constants.DictTypeConstants;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StatusChangeServiceImpl implements StatusChangeService {

    private final StatusChangeMapper statusChangeMapper;
    private final StudentRegistrationService studentRegistrationService;
    private final ClassManagerMapper classManagerMapper;
    private final DictCacheUtil dictCacheUtil;

    /**
     * 审核相关实现
     * 【核心】将审核操作委托给通用的工作流服务
     * 【回调】根据工作流引擎返回的结果，更新业务表
     */
    private final IWorkflowService workflowService; // 【注入】通用工作流服务
    private final List<IStatusChangeHandler> handlers;
    private Map<Long, IStatusChangeHandler> handlerMap;

    private void applyUserPermission(Object queryDTO) {
        UserContext.UserInfo user = UserContext.get();
        System.out.println("当前用户信息: " + user);
        if (user != null && user.getDm() != null) {
            if (queryDTO instanceof CommonQueryDTO dto) {
                dto.setPermissionDm(user.getDm());
            } else if (queryDTO instanceof BasicInfoQueryDTO dto) {
                dto.setPermissionDm(user.getDm());
            } else if (queryDTO instanceof SummaryQueryDTO dto) {
                dto.setPermissionDm(user.getDm());
            }
        }
    }

    @Override
    public PageInfo<StudentBasicInfoVO> getBasicInfo(BasicInfoQueryDTO basicInfoQueryDTO) {
        // 在查询前设置分页信息
        // pageNum：页码，pageSize：每页大小
        applyUserPermission(basicInfoQueryDTO);
        PageHelper.startPage(basicInfoQueryDTO.getPageNum(), basicInfoQueryDTO.getPageSize());

        List<StudentBasicInfoVO> studentInfo = statusChangeMapper.getBasicInfoByKsh(basicInfoQueryDTO);

        // 如果没有找到学生信息，返回空的 PageInfo
        if (studentInfo == null || studentInfo.isEmpty()) {
            return new PageInfo<>(Collections.emptyList());
        }

        // PageInfo 会自动封装 total, pageNum, pageSize 等分页信息
        return new PageInfo<>(studentInfo);
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
        // 走后续逻辑
        studentRegistrationService.createRegistrationByAdmin(registrationInfo);
    }

    @Override
    public PageInfo<LateRegistrationListVO> listLateRegistrations(CommonQueryDTO queryDTO) {
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<LateRegistrationListVO> list = statusChangeMapper.selectLateRegistrationList(queryDTO);
        return new PageInfo<>(list);
    }

    //    休学相关实现
    @Override
    public PageInfo<LeaveAuditListVO> getSuspension(CommonQueryDTO commonQueryDTO) {
        applyUserPermission(commonQueryDTO);
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

        String ksh = leaveApplyDetailDTO.getStudentBasicInfoDTO().getKsh();
        Long kjydlxbsSuspend = 1L; // 1 = 休学

        // 校验1：通用规则 - 是否有正在审核的休学申请
        if (statusChangeMapper.countPendingApplications(ksh, kjydlxbsSuspend) > 0) {
            throw new IllegalStateException("该生有正在审核中的休学申请，请勿重复提交。");
        }

        // 校验2：特定规则 - 当前是否已经是休学状态
        Xfxjl latestRecord = statusChangeMapper.findLatestEffectiveSuspensionOrResumption(ksh);
        if (latestRecord != null && latestRecord.getXfxbj() == 0) { // 0 = 休学
            throw new IllegalStateException("该生当前已处于休学状态，无法再次申请休学。");
        }


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

        String ksh = applyDTO.getStudentBasicInfoDTO().getKsh();
        Long kjydlxbsResume = 2L; // 2 = 复学

        // 校验1：通用规则 - 是否有正在审核的复学申请
        if (statusChangeMapper.countPendingApplications(ksh, kjydlxbsResume) > 0) {
            throw new IllegalStateException("您已有正在审核中的复学申请，请勿重复提交。");
        }

        // 校验2：特定规则 - 当前是否处于可复学的状态（即最近一次记录是休学）
        Xfxjl latestRecord = statusChangeMapper.findLatestEffectiveSuspensionOrResumption(ksh);
        if (latestRecord == null || latestRecord.getXfxbj() != 0) { // 必须有记录，且记录是休学
            throw new IllegalStateException("该生当前不处于休学状态，或没有休学记录，无法申请复学。");
        }

        // 1. 创建总表记录
        Long kjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 2L);
        Xfxjl xiuxue = statusChangeMapper.getLatestXfxjlByKjydjlbs(applyDTO.getStudentBasicInfoDTO().getKsh());

        if (xiuxue == null || xiuxue.getXfxbj() != 0) {
            throw new IllegalArgumentException("该学生没有未复学的休学记录，无法申请复学");
        }

        if (xiuxue.getXxrq().isAfter(applyDTO.getFxrq())) {
            throw new IllegalArgumentException("复学日期不能早于休学日期");
        }

        // 2. 创建 xfxjl 分表记录，并设置复学标记
        Xfxjl xfxjl = new Xfxjl();
        BeanUtils.copyProperties(applyDTO, xfxjl);
        xfxjl.setKjydjlbs(kjydjlbs);
        xfxjl.setXfxbj((byte) 1); // 关键：设置标记为 1 (复学)
        xfxjl.setXxmc(xiuxue.getXxmc());
        xfxjl.setXxrq(xiuxue.getXxrq());
        xfxjl.setXxyy(xiuxue.getXxyy());
        xfxjl.setXxsc(xiuxue.getXxsc());


        statusChangeMapper.insertXfxjl(xfxjl);
    }

    @Override
    public PageInfo<ReturnAuditListVO> listReturnApplications(CommonQueryDTO queryDTO) {
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<ReturnAuditListVO> list = statusChangeMapper.selectReturnList(queryDTO);
        return new PageInfo<>(list);
    }


    // 在 StatusChangeServiceImpl 中实现新方法
    @Override
    @Transactional
    public void updateResumptionDetails(Long kjydjlbs, ResumptionUpdateDTO dto) {


        // 1. 校验主申请记录是否存在，且必须是“待审核”的“复学”申请
        Kjydjl application = statusChangeMapper.findKjydjlById(kjydjlbs);
        if (application == null) {
            throw new IllegalArgumentException("找不到ID为 " + kjydjlbs + " 的申请记录");
        }
        if (application.getKjydlxbs() != 2L) { // 2L = 复学
            throw new IllegalArgumentException("该申请不是复学申请，无法补充信息");
        }
        if (!"待审核".equals(application.getShzt())) {
            throw new IllegalStateException("该申请已审核，无法修改信息");
        }

        // 2. 查找对应的 xfxjl 详情记录
        Xfxjl resumptionDetail = statusChangeMapper.findXfxjlByKjydjlbs(kjydjlbs);
        if (resumptionDetail == null) {
            throw new IllegalStateException("找不到对应的复学详情记录");
        }

        dto.setXbjbs(null);

        Bjxx bjxx = null;

        if (dto.getXbjbs() == null) {
            if (resumptionDetail.getXxmc() != null && dto.getXbjmc() != null) {
                bjxx = classManagerMapper.selectByXxmcAndBjmc(resumptionDetail.getXxmc(), dto.getXbjmc());
                if (bjxx == null) {
                    throw new IllegalArgumentException("班级名称有误，无法找到对应班级信息, 请核对后重新提交");
                }
                dto.setXbjmc(bjxx.getBjmc());
                dto.setXbjbs(bjxx.getBjbs());
            }
        } else {
            // 补充校验：确保新班级属于该学生所在学校
            // TODO: 这里有一个方法可以根据班级标识获取学校名称被注释掉了，正式环境需要启用
            String schooleNameClass = statusChangeMapper.findSchoolNameByBjbs(dto.getXbjbs());
            if (schooleNameClass == null || !schooleNameClass.equals(resumptionDetail.getXxmc())) {
                throw new IllegalArgumentException("所选班级不属于该学生所在学校，请重新选择。");
            }
        }


        // 3. 更新详情记录中的新年级和新班级字段
        resumptionDetail.setXbjmc(dto.getXbjmc());
        resumptionDetail.setXjdnj(dto.getXjdnj());
        resumptionDetail.setXbjbs(dto.getXbjbs());

        // 4. 持久化到数据库
        statusChangeMapper.updateXfxjlDetails(resumptionDetail);
    }

    //   转学相关实现
    // 在 StatusChangeServiceImpl 中实现新方法
    @Override
    @Transactional
    public void updateTransDetails(Long kjydjlbs, TransUpdateDTO dto) {

        // 1. 校验主申请记录是否存在，且必须是“待审核”的“复学”申请
        Kjydjl application = statusChangeMapper.findKjydjlById(kjydjlbs);
        if (application == null) {
            throw new IllegalArgumentException("找不到ID为 " + kjydjlbs + " 的申请记录");
        }
        if (application.getKjydlxbs() != 4L) { // 4L = 转学
            throw new IllegalArgumentException("该申请不是复学申请，无法补充信息");
        }
        if (!"待审核".equals(application.getShzt())) {
            throw new IllegalStateException("该申请已审核，无法修改信息");
        }

        // 2. 查找对应的 xfxjl 详情记录
        Zxjl resumptionDetail = statusChangeMapper.findZxjlByKjydjlbs(kjydjlbs);

        // 补充校验：确保新班级属于该学生所在学校
        // TODO: 这里有一个方法可以根据班级标识获取学校名称被注释掉了，正式环境需要启用
//        String schooleNameClass = statusChangeMapper.findSchoolNameByBjbs(dto.getXbjbs());
//        if(schooleNameClass == null || !schooleNameClass.equals(resumptionDetail.getXxmc())) {
//            throw new IllegalArgumentException("所选班级不属于该学生所在学校，请重新选择。");
//        }
        // 3. 更新详情记录中的新年级和新班级字段


        dto.setXbjbs(null);

        Bjxx bjxx;

        if (dto.getXbjbs() == null) {
            if (resumptionDetail.getXxmc() != null && dto.getXbjmc() != null) {
                bjxx = classManagerMapper.selectByXxmcAndBjmc(resumptionDetail.getXxmc(), dto.getXbjmc());
                if (bjxx == null) {
                    throw new IllegalArgumentException("班级名称有误，无法找到对应班级信息, 请核对后重新提交");
                }
                dto.setXbjmc(bjxx.getBjmc());
                dto.setXbjbs(bjxx.getBjbs());
            }
        } else {
            // 补充校验：确保新班级属于该学生所在学校
            // TODO: 这里有一个方法可以根据班级标识获取学校名称被注释掉了，正式环境需要启用
            String schooleNameClass = statusChangeMapper.findSchoolNameByBjbs(dto.getXbjbs());
            if (schooleNameClass == null || !schooleNameClass.equals(resumptionDetail.getXxmc())) {
                throw new IllegalArgumentException("所选班级不属于该学生所在学校，请重新选择。");
            }
        }

        resumptionDetail.setJdbj(dto.getXbjmc());
        resumptionDetail.setBjbs(dto.getXbjbs());

        // 4. 持久化到数据库
        statusChangeMapper.updateZxjlDetails(resumptionDetail);
    }

    @Override
    public PageInfo<TransferAuditListVO> getTransfer(CommonQueryDTO commonQueryDTO, Long zxlx) {
        applyUserPermission(commonQueryDTO);
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


        String ksh = applyDTO.getStudentBasicInfoDTO().getKsh();
        Long kjydlxbsTransfer = 4L; // 4 = 转学

        if (statusChangeMapper.countPendingApplications(ksh, kjydlxbsTransfer) > 0) {
            throw new IllegalStateException("您已有正在审核中的转学申请，请勿重复提交。");
        }

        if (zxlx == 1 || zxlx == 2) {
            int count = statusChangeMapper.findIfSchoolExistBySchoolName(applyDTO.getXxmc());
            if (count != 1) {
                throw new IllegalArgumentException("转入的学校不存在，请核对后重新提交");
            }
        }

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
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AttritionListVO> list = statusChangeMapper.selectAttritionList(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void applyForAttrition(AttritionApplyDTO applyDTO) {

        String ksh = applyDTO.getStudentBasicInfoDTO().getKsh();
        Long kjydlxbsTransfer = 5L; // 5 = 流失

        if (statusChangeMapper.countPendingApplications(ksh, kjydlxbsTransfer) > 0) {
            throw new IllegalStateException("您已有正在审核中的流失申请，请勿重复提交。");
        }
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
        int count = statusChangeMapper.findIfExistByKsh(studentBasicInfoDTO.getKsh());
        if (count != 1) {
            throw new IllegalArgumentException("考生号不存在，无法创建考籍异动记录");
        }
        Kjydjl kjydjl = new Kjydjl();
        BeanUtils.copyProperties(studentBasicInfoDTO, kjydjl);
        kjydjl.setKjydlxbs(kjydlxbs);
        kjydjl.setShzt("待审核");
        kjydjl.setShjd("待提交");
        statusChangeMapper.insertKjydjl(kjydjl);
        return kjydjl.getKjydjlbs();
    }

    // 出国相关实现
    @Override
    @Transactional
    public void applyForAbroad(AbroadApplyDTO applyDTO) {
        String ksh = applyDTO.getStudentBasicInfoDTO().getKsh();
        Long kjydlxbsTransfer = 7L; // 4 = 转学

        if (statusChangeMapper.countPendingApplications(ksh, kjydlxbsTransfer) > 0) {
            throw new IllegalStateException("您已有正在审核中的出国登记申请，请勿重复提交。");
        }

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

        if(applyDTO.getGgsxmc().equals("民族")){
            if(dictCacheUtil.getCode(DictTypeConstants.MZ, applyDTO.getGhz())==null) {
                throw new IllegalArgumentException("所选民族不存在，请核对后重新提交");
            }
        }else
        if(applyDTO.getGgsxmc().equals("性别")){
            if(!applyDTO.getGhz().equals("男") && !applyDTO.getGhz().equals("女")) {
                throw new IllegalArgumentException("性别只能是“男”或“女”，请核对后重新提交");
            }
        }else
        if(applyDTO.getGgsxmc().equals("姓名")){
            if(applyDTO.getGhz().length()>10) {
                throw new IllegalArgumentException("姓名长度过长，请核对后重新提交");
            }
        }else
        if(applyDTO.getGgsxmc().equals("身份证号")){
            if(!IdCardUtils.isValid(applyDTO.getGhz())) {
                throw new IllegalArgumentException("身份证号码格式不正确，请核对后重新提交");
            }
        }else {
            throw new IllegalArgumentException("非法的变更属性，请核对后重新提交");
        }

        // 1. 创建总表记录
        // 假设“关键属性修改”在 kjydlx 表中的ID为 6L
        Long kjydjlbs = insertKjydjl(applyDTO.getStudentBasicInfoDTO(), 3L);

        // 2. 创建并插入  (详情分表) 记录
        Xxbgjl detail = new Xxbgjl();
        detail.setKjydjlbs(kjydjlbs);
        detail.setGgsxmc(applyDTO.getGgsxmc());
        detail.setGqz(applyDTO.getGqz());
        detail.setGhz(applyDTO.getGhz());
        detail.setZmwjdz(applyDTO.getZmwjdz());

        statusChangeMapper.insertKeyPropertyChange(detail);
    }

    @Override
    public PageInfo<KeyPropertyChangeListVO> listKeyPropertyChangeApps(CommonQueryDTO queryDTO) {
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<KeyPropertyChangeListVO> list = statusChangeMapper.selectKeyPropertyChangeList(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<AbroadListVO> listAbroadApplications(CommonQueryDTO queryDTO) {
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AbroadListVO> abroadList = statusChangeMapper.selectAbroadList(queryDTO);
        return new PageInfo<>(abroadList);
    }

    @Override
    @Transactional
    public String deleteStatusChangeRecord(Long kjydjlbs) {
        // 1. 先从总表查询出这条记录的类型
        KjydjlRecordDTO record = statusChangeMapper.getKjydlxbsByKjydjlbs(kjydjlbs);


        if (record == null) {
            throw new IllegalArgumentException("找不到对应的考籍异动记录，无法删除");
        }


        String shzt = record.getShzt();

        if (!"待审核".equals(shzt)) {
            throw new IllegalStateException("该申请记录已处理，当前状态为：" + shzt + "，无法删除");
        }

        Long recordType = record.getKjydlxbs();


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
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<InformationChangeSummaryVO> list = statusChangeMapper.selectInformationChangeSummary(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<InformationChangeSummaryBySchoolVO> listInformationChangeSummaryBySchool(SummaryQueryDTO queryDTO) {
        applyUserPermission(queryDTO);
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<InformationChangeSummaryBySchoolVO> list = statusChangeMapper.selectInformationChangeSummaryBySchool(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    public List<InformationChangeVO> getInformationChangeListByKsh(String ksh) {
        // 这里可以加入非空校验等业务逻辑
        return statusChangeMapper.selectInformationChangeByKsh(ksh);
    }

    @Override
    public List<StatusChangeVO> getStatusChangeListByKsh(String ksh) {
        // 这里可以加入非空校验等业务逻辑
        return statusChangeMapper.selectStatusChangeByKsh(ksh);
    }

    @PostConstruct
    public void init() {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(IStatusChangeHandler::getActionType, Function.identity()));
        log.info("加载了 {} 个学籍异动善后处理器。", handlerMap.size());
    }

    @Override
    @Transactional
    public void auditApplication(Long kjydjlbs, AuditRequestDTO dto, UserContext.UserInfo currentUser) {
        // 1. 获取业务对象
        Kjydjl application = statusChangeMapper.findKjydjlById(kjydjlbs);
        if (application == null) {
            throw new IllegalStateException("找不到对应的学籍异动申请记录，无法审核");
        }
        if (!"待审核".equals(application.getShzt())) {
            throw new IllegalStateException("该申请已被处理，无法重复审核");
        }

//        // 检查是否是复学申请
//        if (application.getKjydlxbs() == 2L) { // 2L = 复学
//            // 如果是复学申请，并且是“通过”操作，则必须校验新年级和新班级是否已填写
//            if ("通过".equals(dto.getDecision())) {
//                Xfxjl resumptionDetail = statusChangeMapper.findXfxjlByKjydjlbs(kjydjlbs);
//                if (resumptionDetail == null ||
//                        resumptionDetail.getXbjmc() == null || resumptionDetail.getXbjmc().isEmpty() ||
//                        resumptionDetail.getXjdnj() == null || resumptionDetail.getXjdnj().isEmpty() ||
//                        resumptionDetail.getXbjbs() == null) {
//
//                    // 如果信息不完整，则抛出异常，阻止审核继续
//                    throw new IllegalStateException("审核无法通过：请先为该复学申请补充“新年级”和“新班级”信息。");
//                }
//            }
//        }

        String tableName = "";
        if (application.getKjydlxbs() == 1L) {
            tableName = "KJYDJL-1";
        } else if (application.getKjydlxbs() == 2L) {
            tableName = "KJYDJL-2";
        } else if (application.getKjydlxbs() == 3L) {
            tableName = "KJYDJL-3";
        } else if (application.getKjydlxbs() == 4L) {
            tableName = "KJYDJL-4";
        }

        // 2. 【核心】将审核操作委托给通用的工作流服务
        WorkflowResultDTO result = workflowService.processAudit(
                kjydjlbs,              // 业务主键
                tableName,              // 业务表名，用于在auditflow中查找流程
                application.getKsh(),    // 申请人考生号，用于精细化权限校验
                dto,                   // 审核决定和意见
                currentUser            // 当前审核人信息
        );

        application.setShjd(result.getStageName());

        // 3. 【回调】根据工作流引擎返回的结果，更新业务表 kjydjl 的状态
        if (result.getFinalStatus() != null) {
            // 如果流程结束（终审通过或被驳回），更新最终状态
            application.setShzt(result.getFinalStatus());

            // 如果终审通过，执行善后操作
            if ("通过".equals(result.getFinalStatus())) {
                IStatusChangeHandler handler = handlerMap.get(application.getKjydlxbs());
                if (handler != null) {
                    handler.apply(kjydjlbs);
                } else {
                    log.warn("未找到针对异动类型 {} 的善后处理器。", application.getKjydlxbs());
                }
            }
        } else {
            // 如果流程未结束，状态依然是“待审核”，等待下一级处理
            application.setShzt("待审核");
        }

        // 4. 更新公共审核信息并持久化 (逻辑不变)
        application.setShrxm(currentUser.getRealName());
        application.setShyj(dto.getComments());
        application.setShsj(LocalDateTime.now());
        statusChangeMapper.updateAuditInfo(application);
    }

}
