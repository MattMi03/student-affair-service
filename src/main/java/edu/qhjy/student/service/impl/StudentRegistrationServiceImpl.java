package edu.qhjy.student.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.student.domain.Bjxx;
import edu.qhjy.student.domain.Jhrxx;
import edu.qhjy.student.domain.Ksxx;
import edu.qhjy.student.domain.Xlxx;
import edu.qhjy.student.dto.registeration.*;
import edu.qhjy.student.mapper.ClassManagerMapper;
import edu.qhjy.student.mapper.StudentRegistrationMapper;
import edu.qhjy.student.service.StudentRegistrationService;
import edu.qhjy.student.vo.ImportResultVO;
import edu.qhjy.student.vo.StatisticsVO;
import edu.qhjy.student.vo.StudentListVO;
import edu.qhjy.util.IdCardUtils;
import edu.qhjy.util.MobileValidator;
import edu.qhjy.util.cache.DictCacheUtil;
import edu.qhjy.util.constants.DictTypeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

    private final StudentRegistrationMapper registrationMapper;
    private final ClassManagerMapper classManagerMapper;
    private final Executor ioTaskExecutor;
    private final DictCacheUtil dictCacheUtil;

    @Autowired
    public StudentRegistrationServiceImpl(
            StudentRegistrationMapper registrationMapper,
            ClassManagerMapper classManagerMapper,
            @Qualifier("ioTaskExecutor") Executor ioTaskExecutor, DictCacheUtil dictCacheUtil) {
        this.registrationMapper = registrationMapper;
        this.ioTaskExecutor = ioTaskExecutor;
        this.classManagerMapper = classManagerMapper;
        this.dictCacheUtil = dictCacheUtil;
    }


    // ====== 管理员实现 (无需修改) ======
    @Override
    public PageInfo<StudentListVO> listStudentsByPage(AdminStudentQueryDTO queryDTO) {
        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            String permissionDm = user.getDm();
            queryDTO.setPermissionDm(permissionDm);
        }
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
        UserContext.UserInfo user = UserContext.get();
        if (user != null && user.getDm() != null) {
            String permissionDm = user.getDm();
            queryDTO.setPermissionDm(permissionDm);
        }
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
    public void createRegistration(RegistrationInfoDTO registrationInfo) {
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


    /**
     * [REFACTORED] 使用 EasyExcel 生成模板
     */
    @Override
    public byte[] generateExcelTemplate() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EasyExcel.write(out, StudentImportData.class)
                    .sheet("学生信息批量导入模板")
                    .doWrite(new ArrayList<>());
            return out.toByteArray();
        } catch (Exception e) {
            log.error("使用EasyExcel生成模板失败", e);
            throw new RuntimeException("生成Excel模板失败", e);
        }
    }

    @Override
    @Transactional
    public ImportResultVO importFromExcel(MultipartFile file) throws IOException {
        // 使用我们重构后的、有状态的监听器
        StudentImportListener listener = new StudentImportListener(this);
        EasyExcel.read(file.getInputStream(), StudentImportData.class, listener).sheet().doRead();

        List<RegistrationInfoDTO> registrationInfos = listener.getRegistrationList();

        // 调用新的批量处理方法
        return bulkCreateRegistrations(registrationInfos);
    }

    @Transactional
    public ImportResultVO bulkCreateRegistrations(List<RegistrationInfoDTO> registrationInfos) {
        List<String> errorMessages = new ArrayList<>();
        List<String> warningMessages = new ArrayList<>();
        int successCount = 0;

        if (registrationInfos.isEmpty()) {
            return ImportResultVO.builder().totalRows(0).successCount(0).failureCount(0)
                    .errorMessages(errorMessages).warningMessages(warningMessages).build();
        }

        // --- 预处理和校验 ---
        List<String> schoolNames = registrationInfos.stream().map(r -> r.getStudentInfo().getXxmc()).distinct().toList();
        List<Map<String, Object>> schoolDmResultList = registrationMapper.findSchoolDmsByNames(schoolNames);
        Map<String, String> schoolNameToDmMap = schoolDmResultList.stream()
                .filter(m -> m.get("name") != null && m.get("DM") != null)
                .collect(Collectors.toMap(
                        m -> ((String) m.get("name")).trim(),
                        m -> ((String) m.get("DM")).trim(),
                        (v1, v2) -> v1
                ));

        List<String> sfzjhList = registrationInfos.stream().map(r -> r.getStudentInfo().getSfzjh()).toList();
        Set<String> existingSfzjhSet = new HashSet<>(registrationMapper.findExistingSfzjhs(sfzjhList));

        List<Map<String, String>> schoolClassPairs = registrationInfos.stream()
                .map(dto -> {
                    String schoolDm = schoolNameToDmMap.get(dto.getStudentInfo().getXxmc());
                    if (schoolDm != null && StringUtils.hasText(dto.getStudentInfo().getBjmc())) {
                        return Map.of("xxdm", schoolDm, "bjmc", dto.getStudentInfo().getBjmc());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Bjxx> classLookupMap = new HashMap<>();
        if (!schoolClassPairs.isEmpty()) {
            List<Bjxx> existingClasses = classManagerMapper.findClassesBySchoolAndNameBatch(schoolClassPairs);
            classLookupMap = existingClasses.stream()
                    .collect(Collectors.toMap(b -> b.getXxdm() + "|" + b.getBjmc(), b -> b));
        }

        // --- 数据准备循环 (只在内存中操作) ---
        List<Ksxx> studentsToInsert = new ArrayList<>();
        List<Jhrxx> guardiansToInsert = new ArrayList<>();
        List<Xlxx> historiesToInsert = new ArrayList<>();

        for (int i = 0; i < registrationInfos.size(); i++) {
            RegistrationInfoDTO dto = registrationInfos.get(i);
            int rowNum = i + 1;
            String studentNameForLog = (dto.getStudentInfo() != null) ? dto.getStudentInfo().getXm() : "未知姓名";

            try {
                StudentInfoDTO studentInfo = dto.getStudentInfo();
                if (studentInfo == null) throw new IllegalArgumentException("学生基本信息不能为空");

                String sfzjh = studentInfo.getSfzjh();

                // 核心校验
                if (!StringUtils.hasText(sfzjh) || !IdCardUtils.isValid(sfzjh)) {
                    throw new IllegalArgumentException("身份证号不合法或为空");
                }
                if (existingSfzjhSet.contains(sfzjh)) {
                    throw new IllegalStateException("身份证号 " + sfzjh + " 已存在");
                }

                String schoolDm = schoolNameToDmMap.get(studentInfo.getXxmc().trim());
                if (schoolDm == null) {
                    throw new IllegalArgumentException("学校名称 '" + studentInfo.getXxmc() + "' 不存在");
                }

                if (StringUtils.hasText(studentInfo.getBjmc())) {
                    String classLookupKey = schoolDm + "|" + studentInfo.getBjmc();
                    if (!classLookupMap.containsKey(classLookupKey)) {
                        throw new IllegalArgumentException("班级 '" + studentInfo.getBjmc() + "' 在学校 '" + studentInfo.getXxmc() + "' 中不存在");
                    }
                    studentInfo.setBjbs(classLookupMap.get(classLookupKey).getBjbs());
                }

                if (dictCacheUtil.getName(DictTypeConstants.MZ, studentInfo.getMz()) == null) {
                    throw new IllegalArgumentException("民族 '" + studentInfo.getMz() + "' 输入有误, 示例: 汉族");
                }

                // 自动修正并记录警告
                String parsedGender = IdCardUtils.getGender(sfzjh);
                if (StringUtils.hasText(studentInfo.getXb()) && !studentInfo.getXb().equals(parsedGender)) {
                    warningMessages.add("第 " + rowNum + " 行: 学生“" + studentNameForLog + "”的性别与身份证不符，已自动修正");
                }
                studentInfo.setXb(parsedGender);

                LocalDate parsedBirthday = IdCardUtils.getBirthday(sfzjh);
                if (studentInfo.getCsrq() == null || !studentInfo.getCsrq().equals(parsedBirthday)) {
                    warningMessages.add("第 " + rowNum + " 行: 学生“" + studentNameForLog + "”的出生日期与身份证不符，已自动修正");
                }
                studentInfo.setCsrq(parsedBirthday);

                // 准备插入对象
                Ksxx ksxx = convertToKsxx(studentInfo);
                ksxx.setXxdm(schoolDm);
                ksxx.setShzt("已提交待审核");
                studentsToInsert.add(ksxx);

                guardiansToInsert.addAll(dto.getGuardians().stream().map(g -> convertToJhrxx(g, ksxx.getKsh())).toList());
                historiesToInsert.addAll(dto.getAcademicHistories().stream().map(h -> convertToXlxx(h, ksxx.getKsh())).toList());

                successCount++;
            } catch (Exception e) {
                errorMessages.add("第 " + rowNum + " 行 (姓名: " + studentNameForLog + ") 处理失败: " + e.getMessage());
            }
        }

        // --- 批量插入数据库 (在循环外执行) ---
        if (!studentsToInsert.isEmpty()) {
            registrationMapper.batchInsertStudents(studentsToInsert);
        }
        if (!guardiansToInsert.isEmpty()) {
            registrationMapper.batchInsertGuardians(guardiansToInsert);
        }
        if (!historiesToInsert.isEmpty()) {
            registrationMapper.batchInsertAcademicHistories(historiesToInsert);
        }

        return ImportResultVO.builder()
                .totalRows(registrationInfos.size())
                .successCount(successCount)
                .failureCount(errorMessages.size())
                .errorMessages(errorMessages)
                .warningMessages(warningMessages)
                .build();
    }
}