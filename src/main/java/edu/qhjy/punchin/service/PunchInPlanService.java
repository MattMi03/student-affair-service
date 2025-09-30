package edu.qhjy.punchin.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.punchin.dto.PunchInPlanQueryDTO;
import edu.qhjy.punchin.dto.PunchInPlanSubmitDTO;
import edu.qhjy.punchin.vo.PunchInPlanListVO;
import edu.qhjy.punchin.vo.SemestersVO;

/**
 * 打卡计划管理服务接口
 */
public interface PunchInPlanService {

    /**
     * 分页查询打卡计划列表
     *
     * @param query    查询条件 (考区, 学校, 学期名称)
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 封装好的分页结果
     */
    PageInfo<PunchInPlanListVO> listPlans(PunchInPlanQueryDTO query, int pageNum, int pageSize);

    /**
     * 获取某个打卡计划的详细信息
     *
     * @param dkjhbs 打卡计划的主键ID
     * @return 包含打卡计划详情的 DTO
     */
    PunchInPlanSubmitDTO getPlanDetail(Long dkjhbs);


    /**
     * 创建一个新的打卡计划
     *
     * @param dto 包含新计划信息的 DTO (学期, 日期范围, 具体打卡日期等)
     */
    void createPlan(PunchInPlanSubmitDTO dto);

    /**
     * 更新一个已存在的打卡计划
     *
     * @param dkjhbs 要更新的打卡计划的主键ID
     * @param dto    包含最新计划信息的 DTO
     */
    void updatePlan(Long dkjhbs, PunchInPlanSubmitDTO dto);

    /**
     * 删除一个打卡计划
     *
     * @param dkjhbs 要删除的打卡计划的主键ID
     */
    void deletePlan(Long dkjhbs);

    SemestersVO listSemesters();
}
