package edu.qhjy.punchin.mapper;

import edu.qhjy.punchin.domain.Dkjh;
import edu.qhjy.punchin.domain.Dkjl;
import edu.qhjy.punchin.domain.Qjjl;
import edu.qhjy.punchin.dto.LeaveApplicationQueryDTO;
import edu.qhjy.punchin.dto.PunchInRecordQueryDTO;
import edu.qhjy.punchin.dto.PunchInStatsDetailQueryDTO;
import edu.qhjy.punchin.dto.PunchInStatsQueryDTO;
import edu.qhjy.punchin.vo.LeaveApplicationListVO;
import edu.qhjy.punchin.vo.PunchInRecordListVO;
import edu.qhjy.punchin.vo.PunchInStatsDetailVO;
import edu.qhjy.punchin.vo.PunchInStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface PunchInRecordMapper {

    /**
     * 【新增】根据学年名称和学校代码，查找对应的打卡计划
     *
     * @param xnmc 学年/学期名称
     * @param xxdm 学校代码
     * @return 打卡计划实体
     */
    Dkjh findPlanBySemesterAndSchool(@Param("xnmc") String xnmc, @Param("xxdm") String xxdm);

    /**
     * 【新增】批量插入打卡记录
     *
     * @param records 待插入的打卡记录列表
     */
    void batchInsertRecordsIgnoreDuplicates(List<Dkjl> records);

    List<PunchInRecordListVO> findPunchInTasksForPage(PunchInRecordQueryDTO query);

    List<LeaveApplicationListVO> findLeaveApplicationsForPage(LeaveApplicationQueryDTO query);

    void insertLeaveApplication(Qjjl qjjl);

    Qjjl findLeaveApplicationById(Long qjjlbs);

    void updateLeaveApplication(Qjjl qjjl);

    String findSchoolCodeByStudentId(String ksh);


    /**
     * 【新增】分页查询打卡统计数据
     *
     * @param query 查询条件
     * @return 统计结果列表
     */
    List<PunchInStatsVO> findPunchInStats(PunchInStatsQueryDTO query);


    // 在 PunchInRecordMapper 接口中新增方法

    /**
     * 【新增】分页查询打卡统计的详情列表（缺勤/请假学生名单）
     *
     * @param query 查询条件，包含要查询的状态类型
     * @return 详情列表
     */
    List<PunchInStatsDetailVO> findPunchInStatsDetail(PunchInStatsDetailQueryDTO query);


    /**
     * 批量根据考生号查学校代码
     */
    List<Map<String, Object>> findSchoolCodesByKshList(@Param("kshList") List<String> kshList);

    /**
     * 批量查计划（根据学期+学校列表）
     */
    List<Dkjh> findPlansBySemesterAndSchools(@Param("xnmc") String xnmc,
                                             @Param("xxdmList") Set<String> xxdmList);

    /**
     * 批量查计划日期
     */
    List<Map<String, Object>> findPlanDatesByPlanIds(@Param("planIds") Set<Long> planIds);

}
