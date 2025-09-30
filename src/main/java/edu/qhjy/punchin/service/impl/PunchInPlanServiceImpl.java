package edu.qhjy.punchin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.qhjy.punchin.domain.Dkjh;
import edu.qhjy.punchin.dto.PunchInPlanQueryDTO;
import edu.qhjy.punchin.dto.PunchInPlanSubmitDTO;
import edu.qhjy.punchin.mapper.PunchInPlanMapper;
import edu.qhjy.punchin.service.PunchInPlanService;
import edu.qhjy.punchin.vo.PunchInPlanListVO;
import edu.qhjy.punchin.vo.SemestersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PunchInPlanServiceImpl implements PunchInPlanService {

    private final PunchInPlanMapper punchInPlanMapper;

    @Override
    public PageInfo<PunchInPlanListVO> listPlans(PunchInPlanQueryDTO query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PunchInPlanListVO> list = punchInPlanMapper.findForPage(query);
        return new PageInfo<>(list);
    }

    @Override
    public PunchInPlanSubmitDTO getPlanDetail(Long dkjhbs) {
        Dkjh plan = punchInPlanMapper.findById(dkjhbs);
        if (plan == null) {
            throw new IllegalArgumentException("ID为 " + dkjhbs + " 的打卡计划不存在");
        }
        PunchInPlanSubmitDTO dto = new PunchInPlanSubmitDTO();
        BeanUtils.copyProperties(plan, dto);

        List<LocalDate> punchDates = punchInPlanMapper.findPlanDatesByPlanId(dkjhbs);
        dto.setPunchDates(punchDates);

        return dto;
    }

    @Override
    @Transactional
    public void createPlan(PunchInPlanSubmitDTO dto) {

        dto.setXnmc(generateXnmc(dto.getXqksrq()));

        if (dto.getXqjzrq().isBefore(dto.getXqksrq())) {
            throw new IllegalArgumentException("学期截止日期不能早于学期开始日期");
        }

        for (LocalDate date : dto.getPunchDates()) {
            if (date.isBefore(dto.getXqksrq()) || date.isAfter(dto.getXqjzrq())) {
                throw new IllegalArgumentException("打卡日期 " + date + " 不在学期范围内");
            }
        }

        int count = punchInPlanMapper.findSchoolCountByCode(dto.getXxdm());
        if (count == 0) {
            throw new IllegalArgumentException("学校输入有误，未找到对应的学校信息");
        }

        Dkjh dkjh = punchInPlanMapper.findBySubmitDTO(dto);
        if (dkjh != null) {
            throw new IllegalArgumentException("该学期的打卡计划已存在，不能重复创建");
        }

        Dkjh plan = new Dkjh();
        BeanUtils.copyProperties(dto, plan);

        // 1. 插入主表，并获取返回的主键ID
        punchInPlanMapper.insertPlan(plan);

        // 2. 批量插入具体的打卡日期
        punchInPlanMapper.batchInsertPlanDates(plan.getDkjhbs(), dto.getPunchDates());
    }

    @Override
    @Transactional
    public void updatePlan(Long dkjhbs, PunchInPlanSubmitDTO dto) {

        dto.setXnmc(generateXnmc(dto.getXqksrq()));

        if (dto.getXqjzrq().isBefore(dto.getXqksrq())) {
            throw new IllegalArgumentException("学期截止日期不能早于学期开始日期");
        }

        for (LocalDate date : dto.getPunchDates()) {
            if (date.isBefore(dto.getXqksrq()) || date.isAfter(dto.getXqjzrq())) {
                throw new IllegalArgumentException("打卡日期 " + date + " 不在学期范围内");
            }
        }

        Dkjh plan = punchInPlanMapper.findById(dkjhbs);
        if (plan == null) {
            throw new IllegalArgumentException("选择的打卡计划不存在");
        }

        Dkjh dkjh = punchInPlanMapper.findBySubmitDTO(dto);
        if (dkjh != null && dkjh.getDkjhbs() != null && !dkjh.getDkjhbs().equals(dkjhbs)) {
            throw new IllegalArgumentException("该学期的打卡计划已存在，不能重复创建");
        }

        BeanUtils.copyProperties(dto, plan);
        plan.setDkjhbs(dkjhbs);

        // 1. 更新主表信息
        punchInPlanMapper.updatePlan(plan);

        // 2. 更新日期（采用“先删后插”的简单高效策略）
        punchInPlanMapper.deletePlanDatesByPlanId(dkjhbs);
        punchInPlanMapper.batchInsertPlanDates(dkjhbs, dto.getPunchDates());
    }

    @Override
    @Transactional
    public void deletePlan(Long dkjhbs) {
        // 1. 先删除子表的关联日期
        punchInPlanMapper.deletePlanDatesByPlanId(dkjhbs);
        // 2. 再删除主表
        punchInPlanMapper.deletePlanById(dkjhbs);
    }

    private String generateXnmc(LocalDate xqksrq) {
        int year = xqksrq.getYear();
        int month = xqksrq.getMonthValue();

        String xnmc;
        if (month >= 8) {
            // 第一学期：开始年–下一年
            xnmc = String.format("%d-%d-1学期", year, year + 1);
        } else {
            // 第二学期：上一年–开始年
            xnmc = String.format("%d-%d-2学期", year - 1, year);
        }
        return xnmc;
    }

    @Override
    public SemestersVO listSemesters() {
        List<String> semesters = punchInPlanMapper.findAllSemesters();
        SemestersVO vo = new SemestersVO();
        vo.setSemesters(semesters);
        return vo;
    }
}