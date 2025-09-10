package edu.qhjy.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.domain.Jhrxx;
import edu.qhjy.student.domain.Ksxx;
import edu.qhjy.student.domain.Xlxx;
import edu.qhjy.student.dto.registeration.*;
import edu.qhjy.student.mapper.ClassManagerMapper;
import edu.qhjy.student.mapper.StudentRegistrationMapper;
import edu.qhjy.student.service.StudentRegistrationService;
import edu.qhjy.student.vo.StatisticsVO;
import edu.qhjy.student.vo.StudentListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

    private final StudentRegistrationMapper registrationMapper;
    private final ClassManagerMapper classManagerMapper;
    private final Executor ioTaskExecutor;

    @Autowired
    public StudentRegistrationServiceImpl(
            StudentRegistrationMapper registrationMapper,
            ClassManagerMapper classManagerMapper,
            @Qualifier("ioTaskExecutor") Executor ioTaskExecutor) {
        this.registrationMapper = registrationMapper;
        this.ioTaskExecutor = ioTaskExecutor;
        this.classManagerMapper = classManagerMapper;
    }


    // ====== 管理员实现 ======
    @Override
    public PageInfo<StudentListVO> listStudentsByPage(AdminStudentQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<StudentListVO> list = registrationMapper.selectStudentList(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void createRegistrationByAdmin(RegistrationInfoDTO registrationInfo) {
        // 管理员创建的逻辑可能与学生自注册稍有不同（如跳过某些校验），但核心流程复用
        createRegistration(registrationInfo);
    }

    @Override
    @Transactional
    public void updateRegistrationByAdmin(String ksh, RegistrationInfoDTO registrationInfo) {
        updateRegistration(ksh, registrationInfo);
    }

    @Override
    @Transactional
    public void deleteRegistrationByAdmin(String ksh) {
        if (registrationMapper.existsByKsh(ksh) == 0) {
            throw new RuntimeException("考生号 " + ksh + " 不存在，无法删除");
        }
        // 级联删除
        registrationMapper.deleteGuardiansByKsh(ksh);
        registrationMapper.deleteAcademicHistoriesByKsh(ksh);
        registrationMapper.deleteStudentByKsh(ksh);
    }


    @Override
    @Transactional
    public void auditRegistration(String ksh, AuditRequestDTO auditRequestDTO) {
        Ksxx studentToAudit = registrationMapper.findStudentInfoByKsh(ksh);
        if (studentToAudit == null) {
            throw new RuntimeException("考生号 " + ksh + " 不存在，无法审核");
        }

        studentToAudit.setShzt(auditRequestDTO.getShzt());
        studentToAudit.setShyj(auditRequestDTO.getShyj());
        studentToAudit.setShrxm("当前登录管理员姓名"); // TODO: 从Spring Security上下文中获取
        studentToAudit.setShsj(java.time.LocalDateTime.now());

        registrationMapper.updateStudentInfo(studentToAudit);
    }

    @Override
    public PageInfo<StatisticsVO> statistics(AdminStatisticsQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<StatisticsVO> statisticsList = registrationMapper.selectStatistics(queryDTO);
        return new PageInfo<>(statisticsList);
    }


    // ====== 考生与公共实现 ======
    @Override
    public RegistrationInfoDTO getRegistrationInfo(String ksh) {
        // 1. 先同步查询主信息
        Ksxx studentInfoEntity = registrationMapper.findStudentInfoByKsh(ksh);
        if (studentInfoEntity == null) {
            return null; // 学生不存在，直接返回
        }
        StudentInfoDTO studentInfoDTO = convertToStudentInfoDTO(studentInfoEntity);


        // 2. 并行异步查询关联信息
        CompletableFuture<List<Jhrxx>> guardianFuture = CompletableFuture.supplyAsync(
                () -> registrationMapper.findGuardianInfoByKsh(ksh),
                ioTaskExecutor
        );

        CompletableFuture<List<Xlxx>> academicFuture = CompletableFuture.supplyAsync(
                () -> registrationMapper.findAcademicHistoryByKsh(ksh),
                ioTaskExecutor
        );

        // 3. 等待所有并行任务完成
        CompletableFuture.allOf(guardianFuture, academicFuture).join();

        try {
            // 4. 获取结果并组装
            List<GuardianInfoDTO> guardianInfoList = guardianFuture.get().stream()
                    .map(this::convertToGuardianInfoDTO).collect(Collectors.toList());
            List<AcademicHistoryDTO> academicHistoryList = academicFuture.get().stream()
                    .map(this::convertToAcademicHistoryDTO).collect(Collectors.toList());

            while (guardianInfoList.size() < 2) {
                guardianInfoList.add(new GuardianInfoDTO()); // 字段都是 null
            }
            while (academicHistoryList.size() < 2) {
                academicHistoryList.add(new AcademicHistoryDTO()); // 字段都是 null
            }

            RegistrationInfoDTO registrationInfo = new RegistrationInfoDTO();
            registrationInfo.setStudentInfo(studentInfoDTO);
            registrationInfo.setGuardians(guardianInfoList);
            registrationInfo.setAcademicHistories(academicHistoryList);

            return registrationInfo;
        } catch (Exception e) {
            log.error("并行获取考生注册信息时出错, ksh: {}", ksh, e);
            throw new RuntimeException("获取考生注册信息失败", e);
        }
    }

    @Override
    @Transactional
    public void createRegistrationByStudent(String ksh, RegistrationInfoDTO registrationInfo) {
        if (registrationInfo.getStudentInfo() != null) {
            registrationInfo.getStudentInfo().setKsh(ksh); // 确保KSH来自认证信息
        }
        createRegistration(registrationInfo);
    }

    @Override
    @Transactional
    public void updateRegistrationByStudent(String ksh, RegistrationInfoDTO registrationInfo) {
        if (registrationInfo.getStudentInfo() != null) {
            registrationInfo.getStudentInfo().setKsh(ksh); // 确保KSH来自认证信息
        }
        updateRegistration(ksh, registrationInfo);
    }


    // ====== 私有核心方法 (复用您已有的逻辑) ======
    private void createRegistration(RegistrationInfoDTO registrationInfo) {
        StudentInfoDTO studentInfo = registrationInfo.getStudentInfo();
        if (studentInfo == null || studentInfo.getKsh() == null || studentInfo.getKsh().isEmpty()) {
            throw new IllegalArgumentException("考生基本信息或考生号(ksh)不能为空");
        }

        String ksh = studentInfo.getKsh();

        if (registrationMapper.existsByKsh(ksh) > 0) {
            throw new IllegalStateException("该考生号 " + ksh + " 已存在，请勿重复提交");
        }

        Ksxx ksxx = convertToKsxx(studentInfo);

        Bjxx bjxx = classManagerMapper.selectById(ksxx.getBjbs());

        ksxx.setYsyz(bjxx.getYsyz());
        ksxx.setMzyyskyz(bjxx.getMzyyskyz());

        System.out.println("创建报名信息: " + ksxx);
        registrationMapper.insertStudentInfo(ksxx);

        insertSubTableInfo(registrationInfo, ksh);
    }

    private void updateRegistration(String ksh, RegistrationInfoDTO registrationInfo) {
        // 1. 校验
        if (registrationMapper.existsByKsh(ksh) == 0) {
            throw new RuntimeException("考生号 " + ksh + " 不存在，无法更新");
        }

        registrationInfo.getStudentInfo().setKsh(ksh); // 确保KSH来自认证信息

        // 2. 更新考生主信息
        Ksxx ksxx = convertToKsxx(registrationInfo.getStudentInfo());

        Bjxx bjxx = classManagerMapper.selectById(ksxx.getBjbs());

        ksxx.setYsyz(bjxx.getYsyz());
        ksxx.setMzyyskyz(bjxx.getMzyyskyz());
        registrationMapper.updateStudentInfo(ksxx);

        // 3. 删除旧的子表信息
        registrationMapper.deleteGuardiansByKsh(ksh);
        registrationMapper.deleteAcademicHistoriesByKsh(ksh);

        // 4. 插入新的子表信息
        insertSubTableInfo(registrationInfo, ksh);
    }

    private void insertSubTableInfo(RegistrationInfoDTO registrationInfo, String ksh) {
        List<GuardianInfoDTO> guardians = registrationInfo.getGuardians();
        if (!CollectionUtils.isEmpty(guardians)) {
            List<Jhrxx> guardianEntities = guardians.stream()
                    .map(dto -> convertToJhrxx(dto, ksh))
                    .collect(Collectors.toList());
            registrationMapper.insertGuardians(guardianEntities);
        }

        List<AcademicHistoryDTO> academicHistories = registrationInfo.getAcademicHistories();
        if (!CollectionUtils.isEmpty(academicHistories)) {
            List<Xlxx> academicEntities = academicHistories.stream()
                    .map(dto -> convertToXlxx(dto, ksh))
                    .collect(Collectors.toList());
            registrationMapper.insertAcademicHistories(academicEntities);
        }
    }

    // ====== DTO与Entity转换方法 ======
    // 这些转换方法可以使用 MapStruct 等工具自动生成，这里为方便理解手动实现
    private Ksxx convertToKsxx(StudentInfoDTO dto) {
        if (dto == null) return null;
        Ksxx entity = new Ksxx();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private Jhrxx convertToJhrxx(GuardianInfoDTO dto, String ksh) {
        if (dto == null) return null;
        Jhrxx entity = new Jhrxx();
        BeanUtils.copyProperties(dto, entity);
        entity.setKsh(ksh); // 关联外键
        return entity;
    }

    private Xlxx convertToXlxx(AcademicHistoryDTO dto, String ksh) {
        if (dto == null) return null;
        Xlxx entity = new Xlxx();
        BeanUtils.copyProperties(dto, entity);
        entity.setKsh(ksh); // 关联外键
        return entity;
    }

    private StudentInfoDTO convertToStudentInfoDTO(Ksxx entity) {
        if (entity == null) return null;
        StudentInfoDTO dto = new StudentInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private GuardianInfoDTO convertToGuardianInfoDTO(Jhrxx entity) {
        if (entity == null) return null;
        GuardianInfoDTO dto = new GuardianInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private AcademicHistoryDTO convertToAcademicHistoryDTO(Xlxx entity) {
        if (entity == null) return null;
        AcademicHistoryDTO dto = new AcademicHistoryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}