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
import edu.qhjy.util.IdCardUtils;
import edu.qhjy.util.MobileValidator;
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


    // ====== 管理员实现 (无需修改) ======
    @Override
    public PageInfo<StudentListVO> listStudentsByPage(AdminStudentQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<StudentListVO> list = registrationMapper.selectStudentList(queryDTO);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void createRegistrationByAdmin(RegistrationInfoDTO registrationInfo) {
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
        if ("通过".equals(auditRequestDTO.getShzt())) {
            studentToAudit.setShjd("入库成功");
        }

        registrationMapper.updateStudentInfo(studentToAudit);
    }

    @Override
    public PageInfo<StatisticsVO> statistics(AdminStatisticsQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<StatisticsVO> statisticsList = registrationMapper.selectStatistics(queryDTO);
        return new PageInfo<>(statisticsList);
    }


    // ====== 考生与公共实现 (无需修改) ======
    @Override
    public RegistrationInfoDTO getRegistrationInfo(String ksh) {
        Ksxx studentInfoEntity = registrationMapper.findStudentInfoByKsh(ksh);
        if (studentInfoEntity == null) {
            return null;
        }
        StudentInfoDTO studentInfoDTO = convertToStudentInfoDTO(studentInfoEntity);

        CompletableFuture<List<Jhrxx>> guardianFuture = CompletableFuture.supplyAsync(
                () -> registrationMapper.findGuardianInfoByKsh(ksh),
                ioTaskExecutor
        );

        CompletableFuture<List<Xlxx>> academicFuture = CompletableFuture.supplyAsync(
                () -> registrationMapper.findAcademicHistoryByKsh(ksh),
                ioTaskExecutor
        );

        CompletableFuture.allOf(guardianFuture, academicFuture).join();

        try {
            List<GuardianInfoDTO> guardianInfoList = guardianFuture.get().stream()
                    .map(this::convertToGuardianInfoDTO).collect(Collectors.toList());
            List<AcademicHistoryDTO> academicHistoryList = academicFuture.get().stream()
                    .map(this::convertToAcademicHistoryDTO).collect(Collectors.toList());

            while (guardianInfoList.size() < 2) {
                guardianInfoList.add(new GuardianInfoDTO());
            }
            while (academicHistoryList.size() < 2) {
                academicHistoryList.add(new AcademicHistoryDTO());
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
            registrationInfo.getStudentInfo().setKsh(ksh);
        } else {
            throw new IllegalArgumentException("考生基本信息不能为空");
        }
        createRegistration(registrationInfo);
    }

    @Override
    @Transactional
    public void updateRegistrationByStudent(String ksh, RegistrationInfoDTO registrationInfo) {
        if (registrationInfo.getStudentInfo() != null) {
            registrationInfo.getStudentInfo().setKsh(ksh);
        } else {
            throw new IllegalArgumentException("考生基本信息不能为空");
        }

        updateRegistration(ksh, registrationInfo);
    }


    // ====== 私有核心方法 (已按要求重构) ======

    /**
     * [REFACTORED] 已重构，增加根据学校名称查询并设置xxdm的逻辑
     */
    private void createRegistration(RegistrationInfoDTO registrationInfo) {
        StudentInfoDTO studentInfo = registrationInfo.getStudentInfo();
        if (studentInfo == null || studentInfo.getKsh() == null || studentInfo.getKsh().isEmpty()) {
            throw new IllegalArgumentException("考生基本信息或考生号(ksh)不能为空");
        }

        String ksh = studentInfo.getKsh();

        if (registrationMapper.existsByKsh(ksh) > 0) {
            throw new IllegalStateException("该考生号 " + ksh + " 已存在，请勿重复提交");
        }

        studentInfo.setCsrq(IdCardUtils.getBirthday(studentInfo.getSfzjh()));
        studentInfo.setXb(IdCardUtils.getGender(studentInfo.getSfzjh()));

        Ksxx ksxx = convertToKsxx(studentInfo);

        // [REFACTORED] START: 新增逻辑，根据学校名称查询并设置XXDM
        if (studentInfo.getXxmc() != null && !studentInfo.getXxmc().isEmpty()) {
            String schoolDm = registrationMapper.findSchoolDmBySchoolName(studentInfo.getXxmc());
            if (schoolDm == null) {
                throw new IllegalArgumentException("根据学校名称 '" + studentInfo.getXxmc() + "' 未在单位代码库(XYZDK)中找到对应的学校代码。");
            }
            ksxx.setXxdm(schoolDm); // 设置XXDM
        }
        // [REFACTORED] END

        Bjxx bjxx = null;
        if (studentInfo.getBjbs() == null) {
            if (studentInfo.getXxmc() != null && studentInfo.getBjmc() != null) {
                bjxx = classManagerMapper.selectByXxmcAndBjmc(studentInfo.getXxmc(), studentInfo.getBjmc());
                if (bjxx == null) {
                    throw new IllegalArgumentException("学校名称或班级名称有误，无法找到对应班级信息, 请核对后重新提交, 如未分班暂不填写");
                }
            }
        } else {
            bjxx = classManagerMapper.selectById(ksxx.getBjbs());
        }

        if (bjxx != null) {
            ksxx.setYsyz(bjxx.getYsyz());
            ksxx.setMzyyskyz(bjxx.getMzyyskyz());
            ksxx.setBjbs(bjxx.getBjbs());
            ksxx.setBjmc(bjxx.getBjmc());
        }
        ksxx.setShzt("已提交待审核");

        registrationMapper.insertStudentInfo(ksxx);

        insertSubTableInfo(registrationInfo, ksh);
    }

    /**
     * [REFACTORED] 已重构，增加根据学校名称查询并设置xxdm的逻辑
     */
    private void updateRegistration(String ksh, RegistrationInfoDTO registrationInfo) {
        if (registrationMapper.existsByKsh(ksh) == 0) {
            throw new RuntimeException("考生号 " + ksh + " 不存在，无法更新");
        }

        StudentInfoDTO studentInfo = registrationInfo.getStudentInfo();
        studentInfo.setKsh(ksh);

        studentInfo.setCsrq(IdCardUtils.getBirthday(studentInfo.getSfzjh()));
        studentInfo.setXb(IdCardUtils.getGender(studentInfo.getSfzjh()));

        Ksxx ksxx = convertToKsxx(studentInfo);

        // [REFACTORED] START: 新增逻辑，根据学校名称查询并设置XXDM
        if (studentInfo.getXxmc() != null && !studentInfo.getXxmc().isEmpty()) {
            String schoolDm = registrationMapper.findSchoolDmBySchoolName(studentInfo.getXxmc());
            if (schoolDm == null) {
                throw new IllegalArgumentException("根据学校名称 '" + studentInfo.getXxmc() + "' 未在单位代码库(XYZDK)中找到对应的学校代码。");
            }
            ksxx.setXxdm(schoolDm); // 设置XXDM
        }
        // [REFACTORED] END

        Bjxx bjxx = null;
        if (studentInfo.getBjbs() == null) {
            if (studentInfo.getXxmc() != null && studentInfo.getBjmc() != null) {
                bjxx = classManagerMapper.selectByXxmcAndBjmc(studentInfo.getXxmc(), studentInfo.getBjmc());
                if (bjxx == null) {
                    throw new IllegalArgumentException("学校名称或班级名称有误，无法找到对应班级信息, 请核对后重新提交");
                }
            }
        } else {
            bjxx = classManagerMapper.selectById(ksxx.getBjbs());
        }

        if (bjxx != null) {
            ksxx.setYsyz(bjxx.getYsyz());
            ksxx.setMzyyskyz(bjxx.getMzyyskyz());
            ksxx.setBjbs(bjxx.getBjbs());
            ksxx.setBjmc(bjxx.getBjmc());
        }
        registrationMapper.updateStudentInfo(ksxx);

        registrationMapper.deleteGuardiansByKsh(ksh);
        registrationMapper.deleteAcademicHistoriesByKsh(ksh);

        insertSubTableInfo(registrationInfo, ksh);
    }

    // ====== 私有辅助方法 (无需修改) ======

    private void insertSubTableInfo(RegistrationInfoDTO registrationInfo, String ksh) {
        List<GuardianInfoDTO> guardians = registrationInfo.getGuardians();
        if (!CollectionUtils.isEmpty(guardians)) {
            List<Jhrxx> guardianEntities = guardians.stream()
                    .map(dto -> convertToJhrxx(dto, ksh))
                    .collect(Collectors.toList());
            for (Jhrxx jhrxx : guardianEntities) {
                IdCardUtils.validate(jhrxx.getSfzjh());
                MobileValidator.validateOrThrow(jhrxx.getYddh());
            }
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
        entity.setKsh(ksh);
        return entity;
    }

    private Xlxx convertToXlxx(AcademicHistoryDTO dto, String ksh) {
        if (dto == null) return null;
        Xlxx entity = new Xlxx();
        BeanUtils.copyProperties(dto, entity);
        entity.setKsh(ksh);
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