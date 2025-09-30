package edu.qhjy.punchin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.punchin.domain.Dkjh;
import edu.qhjy.punchin.domain.Dkjl;
import edu.qhjy.punchin.domain.Qjjl;
import edu.qhjy.punchin.dto.*;
import edu.qhjy.punchin.mapper.PunchInRecordMapper;
import edu.qhjy.punchin.service.PunchInRecordService;
import edu.qhjy.punchin.vo.LeaveApplicationListVO;
import edu.qhjy.punchin.vo.PunchInRecordListVO;
import edu.qhjy.punchin.vo.PunchInStatsDetailVO;
import edu.qhjy.punchin.vo.PunchInStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PunchInRecordServiceImpl implements PunchInRecordService {

    private final PunchInRecordMapper punchInRecordMapper;

    @Override
    public PageInfo<PunchInRecordListVO> listRecords(PunchInRecordQueryDTO query) {
        // 1. 启动 PageHelper 分页
        PageHelper.startPage(query.getPageNum(), query.getPageSize());

        // 2. 【核心】直接调用重构后的Mapper方法，获取已在数据库层面生成好的、可直接分页的“任务列表”
        List<PunchInRecordListVO> list = punchInRecordMapper.findPunchInTasksForPage(query);

        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("没有找到符合条件的打卡记录,可能该学期还没有打卡计划");
        }

        // 3. PageHelper 会自动处理总数、页码等信息，直接封装并返回
        return new PageInfo<>(list);
    }


    @Override
    @Transactional
    public Map<String, Object> importRecordsFromJson(List<PunchInImportDTO> importList) {
        if (CollectionUtils.isEmpty(importList)) {
            throw new IllegalArgumentException("导入的数据列表不能为空");
        }

        List<Dkjl> recordsToInsert = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

// 1. 批量查学校
        List<String> kshList = importList.stream()
                .map(PunchInImportDTO::getKsh)
                .collect(Collectors.toList());
        Map<String, String> kshToXxdm = punchInRecordMapper.findSchoolCodesByKshList(kshList)
                .stream()
                .collect(Collectors.toMap(m -> (String) m.get("KSH"), m -> (String) m.get("XXDM")));

// 2. 批量查计划
        String xnmc = generateXnmc(importList.get(0).getDkrq()); // 这里假设都在同一学期
        Set<String> xxdmList = new HashSet<>(kshToXxdm.values());
        Map<String, Dkjh> xxdmToPlan = punchInRecordMapper.findPlansBySemesterAndSchools(xnmc, xxdmList)
                .stream()
                .collect(Collectors.toMap(Dkjh::getXxdm, p -> p));

// 3. 批量查计划日期
        Set<Long> planIds = xxdmToPlan.values().stream()
                .map(Dkjh::getDkjhbs)
                .collect(Collectors.toSet());
        Map<Long, Set<LocalDate>> planIdToDates = new HashMap<>();
        for (Map<String, Object> row : punchInRecordMapper.findPlanDatesByPlanIds(planIds)) {
            Long planId = ((Number) row.get("DKJHBS")).longValue();
            java.sql.Date sqlDate = (java.sql.Date) row.get("PUNCH_DATE");
            LocalDate date = sqlDate.toLocalDate();
            planIdToDates.computeIfAbsent(planId, k -> new HashSet<>()).add(date);
        }

// 4. 遍历导入数据（只走内存 Map 校验）
        for (int i = 0; i < importList.size(); i++) {
            PunchInImportDTO dto = importList.get(i);
            int rowNum = i + 1;

            try {
                String xxdm = kshToXxdm.get(dto.getKsh());
                if (xxdm == null) throw new IllegalArgumentException("考籍号 [" + dto.getKsh() + "] 不存在");

                Dkjh plan = xxdmToPlan.get(xxdm);
                if (plan == null) throw new IllegalArgumentException("找不到考生 [" + dto.getKsh() + "] 的打卡计划");

                Set<LocalDate> validDates = planIdToDates.getOrDefault(plan.getDkjhbs(), Collections.emptySet());
                if (!validDates.contains(dto.getDkrq())) {
                    throw new IllegalArgumentException("日期 [" + dto.getDkrq() + "] 不是计划内有效打卡日");
                }

                Dkjl record = new Dkjl();
                record.setDkjhbs(plan.getDkjhbs());
                record.setDkrq(dto.getDkrq());
                record.setKsh(dto.getKsh());
                record.setDksj(dto.getDksj());
                record.setDkdd(dto.getDkdd());
                record.setDksb(dto.getDksb());
                recordsToInsert.add(record);

            } catch (Exception e) {
                errorMessages.add("第 " + rowNum + " 行数据处理失败: " + e.getMessage());
            }
        }

        // --- 批量插入 ---
        if (!recordsToInsert.isEmpty()) {
            punchInRecordMapper.batchInsertRecordsIgnoreDuplicates(recordsToInsert);
        }

        // --- 构造返回结果 ---
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", importList.size());
        result.put("successCount", recordsToInsert.size());
        result.put("failureCount", errorMessages.size());
        result.put("errorMessages", errorMessages);

        return result;
    }

    // =================================================================
    // 【新增】请假管理相关服务实现
    // =================================================================

    @Override
    public PageInfo<LeaveApplicationListVO> listLeaveApplications(LeaveApplicationQueryDTO query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<LeaveApplicationListVO> list = punchInRecordMapper.findLeaveApplicationsForPage(query);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void createLeaveApplication(LeaveApplicationSubmitDTO dto, String ksh) {
        // 1. 【新增】根据 ksh 获取学生所在的学校代码 (xxdm)
        String xxdm = punchInRecordMapper.findSchoolCodeByStudentId(ksh);
        if (xxdm == null) {
            throw new NoSuchElementException("未找到考生 " + ksh + " 对应的学校信息");
        }

        // 2. 【新增】根据请假开始日期，自动生成学年名称
        String xnmc = generateXnmc(dto.getKsrq());

        // 3. 【新增】根据学年名称和学校代码，查找对应的打卡计划
        Dkjh plan = punchInRecordMapper.findPlanBySemesterAndSchool(xnmc, xxdm);
        if (plan == null) {
            throw new IllegalStateException("未找到学校代码为 " + xxdm + " 且学期为 " + xnmc + " 的打卡计划，无法提交请假申请");
        }

        // 4. 创建并插入请假记录
        Qjjl application = new Qjjl();
        BeanUtils.copyProperties(dto, application);
        application.setKsh(ksh);
        application.setDkjhbs(plan.getDkjhbs()); // 【关键】设置自动查找到的计划ID

        punchInRecordMapper.insertLeaveApplication(application);
    }

    /**
     * 【新增】根据日期自动生成学年/学期名称的私有辅助方法
     */
    private String generateXnmc(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        String xnmc;
        if (month >= 8) { // 8月及以后，属于新学年的第一学期
            xnmc = String.format("%d-%d-1学期", year, year + 1);
        } else { // 8月以前，属于上一学年的第二学期
            xnmc = String.format("%d-%d-2学期", year - 1, year);
        }
        return xnmc;
    }

    @Override
    @Transactional
    public void auditLeaveApplication(Long qjjlbs, LeaveApplicationAuditDTO dto, String auditorName) {
        Qjjl application = punchInRecordMapper.findLeaveApplicationById(qjjlbs);
        if (application == null) {
            throw new NoSuchElementException("找不到ID为 " + qjjlbs + " 的请假申请");
        }
        if (!"待审核".equals(application.getShzt())) {
            throw new IllegalStateException("该申请已被处理，请勿重复审核");
        }

        application.setShzt(dto.getShzt());
        application.setShyj(dto.getShyj());
        application.setShrxm(auditorName);
        application.setShsj(LocalDateTime.now());

        punchInRecordMapper.updateLeaveApplication(application);
    }

    // 在 PunchInRecordServiceImpl 中新增方法实现
    @Override
    public PageInfo<PunchInStatsVO> getPunchInStats(PunchInStatsQueryDTO query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PunchInStatsVO> list = punchInRecordMapper.findPunchInStats(query);
        return new PageInfo<>(list);
    }

    // 在 PunchInRecordServiceImpl 中新增方法实现
    @Override
    public PageInfo<PunchInStatsDetailVO> getPunchInStatsDetail(PunchInStatsDetailQueryDTO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PunchInStatsDetailVO> list = punchInRecordMapper.findPunchInStatsDetail(query);
        return new PageInfo<>(list);
    }
}