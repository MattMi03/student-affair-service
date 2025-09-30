package edu.qhjy.punchin.controller;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.punchin.dto.*;
import edu.qhjy.punchin.service.PunchInRecordService;
import edu.qhjy.punchin.vo.LeaveApplicationListVO;
import edu.qhjy.punchin.vo.PunchInRecordListVO;
import edu.qhjy.punchin.vo.PunchInStatsDetailVO;
import edu.qhjy.punchin.vo.PunchInStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/punch-in-records")
@RequiredArgsConstructor
@Tag(name = "打卡记录状态管理", description = "查询学生的打卡记录")
public class PunchInRecordController {

    private final PunchInRecordService punchInRecordService;

    @GetMapping
    @Operation(summary = "分页查询打卡记录", description = "根据查询条件分页查询打卡记录")
    public Result<PageInfo<PunchInRecordListVO>> list(PunchInRecordQueryDTO query) {
        return Result.success(punchInRecordService.listRecords(query));
    }

    /**
     * 【重构】通过JSON批量导入打卡记录
     *
     * @param importList 包含多条打卡记录的JSON数组
     * @return 导入结果
     */
    @PostMapping("/records/import-json")
    @Operation(summary = "导入打卡记录 (JSON)", description = "接收JSON格式的打卡记录数组进行批量导入，用于设备接入或前端解析后的数据提交。")
    public Result<Map<String, Object>> importRecordsFromJson(@RequestBody List<PunchInImportDTO> importList) {
        try {
            Map<String, Object> result = punchInRecordService.importRecordsFromJson(importList);
            return Result.success("导入处理完成", result);
        } catch (Exception e) {
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }

    // --- 新增的请假管理接口 ---

    @GetMapping("/leave-applications")
    @Operation(summary = "【管理端】分页查询请假申请列表")
    public Result<PageInfo<LeaveApplicationListVO>> listLeaveApplications(
            LeaveApplicationQueryDTO query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(punchInRecordService.listLeaveApplications(query, pageNum, pageSize));
    }

    @PostMapping("/leave-applications")
    @Operation(summary = "【管理端】提交请假申请")
    public Result<?> createLeaveApplication(@Validated @RequestBody LeaveApplicationSubmitDTO dto) {
        punchInRecordService.createLeaveApplication(dto, dto.getKsh());
        return Result.success("请假申请提交成功");
    }

    @PostMapping("/leave-applications/{qjjlbs}/audit")
    @Operation(summary = "【管理端】审核请假申请")
    public Result<?> auditLeaveApplication(HttpServletRequest request, @PathVariable Long qjjlbs, @Validated @RequestBody LeaveApplicationAuditDTO dto) {
        // 在实际项目中，审核人姓名应从安全上下文中获取
        String auditorName = request.getHeader("x-user-realname");
        punchInRecordService.auditLeaveApplication(qjjlbs, dto, auditorName);
        return Result.success("审核操作成功");
    }

    @GetMapping("/stats")
    @Operation(summary = "分页查询打卡统计")
    public Result<PageInfo<PunchInStatsVO>> getStats(
            PunchInStatsQueryDTO query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(punchInRecordService.getPunchInStats(query, pageNum, pageSize));
    }

    /**
     * 【新增】分页查询打卡统计详情 (缺勤/请假名单)
     */
    @GetMapping("/stats/detail")
    @Operation(summary = "查询打卡统计详情")
    public Result<PageInfo<PunchInStatsDetailVO>> getStatsDetail(PunchInStatsDetailQueryDTO query) {
        return Result.success(punchInRecordService.getPunchInStatsDetail(query));
    }
}







