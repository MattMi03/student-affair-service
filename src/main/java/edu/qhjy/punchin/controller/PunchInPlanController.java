package edu.qhjy.punchin.controller;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.punchin.dto.PunchInPlanQueryDTO;
import edu.qhjy.punchin.dto.PunchInPlanSubmitDTO;
import edu.qhjy.punchin.service.PunchInPlanService;
import edu.qhjy.punchin.vo.PunchInPlanListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/punch")
@RequiredArgsConstructor
@Tag(name = "打卡计划管理", description = "打卡计划的增删改查接口")
public class PunchInPlanController {

    private final PunchInPlanService punchInPlanService;

    @GetMapping
    @Operation(summary = "分页查询打卡计划", description = "根据考区代码、学校代码和学期名称分页查询打卡计划")
    public Result<PageInfo<PunchInPlanListVO>> list(PunchInPlanQueryDTO query,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(punchInPlanService.listPlans(query, pageNum, pageSize));
    }

    @GetMapping("/detail/{dkjhbs}")
    @Operation(summary = "获取打卡计划详情", description = "根据打卡计划ID获取打卡计划的详细信息")
    public Result<PunchInPlanSubmitDTO> getDetail(@PathVariable Long dkjhbs) {
        return Result.success(punchInPlanService.getPlanDetail(dkjhbs));
    }

    @PostMapping
    @Operation(summary = "新增打卡计划", description = "创建新的打卡计划，包含学年名称、学期开始和结束日期，以及具体的打卡日期列表")
    public Result<?> create(@Validated @RequestBody PunchInPlanSubmitDTO dto) {
        punchInPlanService.createPlan(dto);
        return Result.success("创建成功");
    }

    @PutMapping("/{dkjhbs}")
    @Operation(summary = "更新打卡计划", description = "根据打卡计划ID更新打卡计划的学年名称、学期日期和打卡日期列表")
    public Result<?> update(@PathVariable Long dkjhbs, @Validated @RequestBody PunchInPlanSubmitDTO dto) {
        punchInPlanService.updatePlan(dkjhbs, dto);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{dkjhbs}")
    @Operation(summary = "删除打卡计划", description = "根据打卡计划ID删除对应的打卡计划及其所有关联的打卡日期")
    public Result<?> delete(@PathVariable Long dkjhbs) {
        punchInPlanService.deletePlan(dkjhbs);
        return Result.success("删除成功");
    }

    // 学期列表
    @GetMapping("/semesters")
    @Operation(summary = "获取学期列表", description = "获取所有可用的学期名称列表")
    public Result<?> listSemesters() {
        return Result.success(punchInPlanService.listSemesters());
    }
}