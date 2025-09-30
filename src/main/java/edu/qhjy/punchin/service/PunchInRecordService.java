package edu.qhjy.punchin.service;

import com.github.pagehelper.PageInfo;
import edu.qhjy.punchin.dto.*;
import edu.qhjy.punchin.vo.LeaveApplicationListVO;
import edu.qhjy.punchin.vo.PunchInRecordListVO;
import edu.qhjy.punchin.vo.PunchInStatsDetailVO;
import edu.qhjy.punchin.vo.PunchInStatsVO;

import java.util.List;
import java.util.Map;

public interface PunchInRecordService {

    /**
     * 分页查询打卡记录
     *
     * @param query 查询条件
     * @return 分页的打卡记录列表
     */
    PageInfo<PunchInRecordListVO> listRecords(PunchInRecordQueryDTO query);


    /**
     * 【重构】通过JSON批量导入打卡记录
     *
     * @param importList 包含多条打卡记录的列表
     * @return 导入结果，包含成功和失败的数量及原因
     */
    Map<String, Object> importRecordsFromJson(List<PunchInImportDTO> importList);

    // =================================================================
    // 【新增】请假管理相关服务
    // =================================================================

    /**
     * 【新增】分页查询请假申请列表 (管理端)
     *
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页的请假申请列表
     */
    PageInfo<LeaveApplicationListVO> listLeaveApplications(LeaveApplicationQueryDTO query, int pageNum, int pageSize);

    /**
     * 【新增】创建一条请假申请 (学生端)
     *
     * @param dto 包含申请信息的 DTO
     * @param ksh 申请学生的考生号
     */
    void createLeaveApplication(LeaveApplicationSubmitDTO dto, String ksh);

    /**
     * 【新增】审核一条请假申请 (管理端)
     *
     * @param qjjlbs      请假记录的ID
     * @param dto         审核决定和意见
     * @param auditorName 审核人姓名
     */
    void auditLeaveApplication(Long qjjlbs, LeaveApplicationAuditDTO dto, String auditorName);

    // 在 PunchInRecordService 接口中新增方法

    /**
     * 【新增】分页查询打卡统计
     *
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页的统计结果
     */
    PageInfo<PunchInStatsVO> getPunchInStats(PunchInStatsQueryDTO query, int pageNum, int pageSize);

    // 在 PunchInRecordService 接口中新增方法

    /**
     * 【新增】分页查询打卡统计详情
     *
     * @param query 查询条件
     * @return 分页的详情结果
     */
    PageInfo<PunchInStatsDetailVO> getPunchInStatsDetail(PunchInStatsDetailQueryDTO query);
}