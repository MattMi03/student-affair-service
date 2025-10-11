package edu.qhjy.student.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import edu.qhjy.student.dto.registeration.*;
import edu.qhjy.student.service.StudentRegistrationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class StudentImportListener extends AnalysisEventListener<StudentImportData> {

    private final List<RegistrationInfoDTO> registrationList = new ArrayList<>();

    private final StudentRegistrationService studentRegistrationService;

    public StudentImportListener(StudentRegistrationService studentRegistrationService) {
        this.studentRegistrationService = studentRegistrationService;
    }

    /**
     * 每解析一行数据（一个学生），会调用此方法
     */
    @Override
    public void invoke(StudentImportData data, AnalysisContext context) {
        // 如果关键字段为空，则视为空行，跳过
        if (!StringUtils.hasText(data.getXm()) && !StringUtils.hasText(data.getSfzjh())) {
            return;
        }

        RegistrationInfoDTO registrationInfo = new RegistrationInfoDTO();

        // 1. 解析学生信息
        StudentInfoDTO studentInfo = new StudentInfoDTO();
        studentInfo.setXm(data.getXm());
        studentInfo.setSfzjh(data.getSfzjh());
        studentInfo.setXb(data.getXb());
        studentInfo.setMz(data.getMz());
        studentInfo.setCsrq(parseDate(data.getCsrq()));
        studentInfo.setXjh(data.getXjh());
        studentInfo.setSzxmc(data.getSzxmc());
        studentInfo.setXxmc(data.getXxmc());
        studentInfo.setBjmc(data.getBjmc());
        studentInfo.setKjztmc(data.getKjztmc());
        registrationInfo.setStudentInfo(studentInfo);

        // 2. 解析监护人信息（只在有姓名时添加）
        List<GuardianInfoDTO> guardians = new ArrayList<>();
        if (StringUtils.hasText(data.getGuardian1Xm())) {
            GuardianInfoDTO g1 = new GuardianInfoDTO();
            g1.setGx(data.getGuardian1Gx());
            g1.setXm(data.getGuardian1Xm());
            g1.setMz(data.getGuardian1Mz());
            g1.setSfzjh(data.getGuardian1Sfzjh());
            g1.setSzxmc(data.getGuardian1Szxmc());
            g1.setGzdwmc(data.getGuardian1Gzdwmc());
            g1.setGzdwzw(data.getGuardian1Gzdwzw());
            g1.setYddh(data.getGuardian1Yddh());
            guardians.add(g1);
        }
        if (StringUtils.hasText(data.getGuardian2Xm())) {
            GuardianInfoDTO g2 = new GuardianInfoDTO();
            g2.setGx(data.getGuardian2Gx());
            g2.setXm(data.getGuardian2Xm());
            g2.setMz(data.getGuardian2Mz());
            g2.setSfzjh(data.getGuardian2Sfzjh());
            g2.setSzxmc(data.getGuardian2Szxmc());
            g2.setGzdwmc(data.getGuardian2Gzdwmc());
            g2.setGzdwzw(data.getGuardian2Gzdwzw());
            g2.setYddh(data.getGuardian2Yddh());
            guardians.add(g2);
        }
        registrationInfo.setGuardians(guardians);

        // 3. 解析学历信息（只在有学历阶段时添加）
        List<AcademicHistoryDTO> histories = new ArrayList<>();
        if (StringUtils.hasText(data.getHistory1Xl())) {
            AcademicHistoryDTO h1 = new AcademicHistoryDTO();
            h1.setXl(data.getHistory1Xl());
            h1.setKsrq(parseDate(data.getHistory1Ksrq()));
            h1.setXxmc(data.getHistory1Xxmc());
            h1.setZmrxm(data.getHistory1Zmrxm());
            histories.add(h1);
        }
        if (StringUtils.hasText(data.getHistory2Xl())) {
            AcademicHistoryDTO h2 = new AcademicHistoryDTO();
            h2.setXl(data.getHistory2Xl());
            h2.setKsrq(parseDate(data.getHistory2Ksrq()));
            h2.setXxmc(data.getHistory2Xxmc());
            h2.setZmrxm(data.getHistory2Zmrxm());
            histories.add(h2);
        }
        registrationInfo.setAcademicHistories(histories);

        // 将完整解析的DTO对象添加到最终列表中
        this.registrationList.add(registrationInfo);

        System.out.println(registrationInfo);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成，共解析出 {} 条学生注册记录。", registrationList.size());
    }

    private LocalDate parseDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } catch (Exception e2) {
                log.warn("日期格式无法解析: {}", dateStr);
                return null;
            }
        }
    }
}