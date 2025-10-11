package edu.qhjy.statuschange.controller.admin;

import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.common.Result;
import edu.qhjy.statuschange.dto.*;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.audit.ResumptionUpdateDTO;
import edu.qhjy.statuschange.dto.audit.TransUpdateDTO;
import edu.qhjy.statuschange.service.StatusChangeService;
import edu.qhjy.statuschange.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/admin/status-changes")
@AllArgsConstructor
@Tag(name = "管理学籍异动变更接口", description = "提供学生状态变更的管理功能，包括查询、创建、更新和删除学生状态变更信息")
public class AdminStatusChangeController {

    private final StatusChangeService statusChangeService;

    @GetMapping("/basic-info")
    @Operation(summary = "获取学生基本信息", description = "根据考生号获取学生的基本信息")
    public Result<Object> getBasicInfo(BasicInfoQueryDTO basicInfoQueryDTO) {
        // 调用服务层方法获取学生基本信息
        var studentInfo = statusChangeService.getBasicInfo(basicInfoQueryDTO);

        // 检查是否成功获取到学生信息
        if (studentInfo == null) {
            return Result.error("未找到学生信息");
        }

        // 返回成功结果
        return Result.success(studentInfo);
    }

    // 补录相关
    @PostMapping("/late-registration")
    @Operation(summary = "创建新生补录申请", description = "创建一个待审核的新生补录记录")
    public Result<Void> createLateRegistration(@Validated @RequestBody LateRegistrationApplyDTO lateRegistrationApplyDTO) {
        statusChangeService.createLateRegistration(lateRegistrationApplyDTO);
        return Result.success("新生补录申请提交成功");
    }

    @GetMapping("/late-registration")
    @Operation(summary = "查询新生补录列表", description = "分页查询所有需要补录审核的学生")
    public Result<PageInfo<LateRegistrationListVO>> listLateRegistrations(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.listLateRegistrations(queryDTO));
    }

    // 休学相关
    @GetMapping("/suspension")
    @Operation(summary = "获取休学审核列表", description = "根据查询条件获取休学审核列表")
    public Result<Object> getSuspension(CommonQueryDTO commonQueryDTO) {
        PageInfo<LeaveAuditListVO> leaveAuditListVO = statusChangeService.getSuspension(commonQueryDTO);
        if (leaveAuditListVO == null) {
            return Result.error("未找到请假审核信息");
        } else {
            return Result.success(leaveAuditListVO);
        }
    }

    @PostMapping("/suspension")
    @Operation(summary = "创建休学记录", description = "根据休学申请详情创建休学记录")
    public Result<Object> createSuspension(@Validated @RequestBody LeaveApplyDetailDTO leaveApplyDetailDTO) {
        statusChangeService.createSuspension(leaveApplyDetailDTO);

        return Result.success("休学记录创建成功");
    }

    // 复学相关
    @PostMapping("/return-school")
    @Operation(summary = "创建新的复学申请")
    public Result<Void> applyForReturn(@Validated @RequestBody ReturnApplyDTO applyDTO) {
        statusChangeService.applyForReturn(applyDTO);
        return Result.success("复学申请提交成功");
    }

    @GetMapping("/return-school")
    @Operation(summary = "查询复学申请列表")
    public Result<PageInfo<ReturnAuditListVO>> listReturnApplications(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.listReturnApplications(queryDTO));
    }

    /**
     * 【新增】为复学申请补充新年级和新班级信息
     *
     * @param kjydjlbs 考籍异动记录的ID
     * @param dto      包含新年级和新班级的数据
     * @return 成功响应
     */
    @PutMapping("/return-school/{kjydjlbs}")
    @Operation(summary = "补充复学申请信息", description = "在审核前，由管理员为复学申请填入分配好的新年级和新班级。")
    public Result<Void> updateResumptionDetails(
            @PathVariable Long kjydjlbs,
            @Validated @RequestBody ResumptionUpdateDTO dto) {

        // 调用Service层执行更新操作
        statusChangeService.updateResumptionDetails(kjydjlbs, dto);
        return Result.success("复学信息补充成功");
    }


    // 转学相关

    /**
     * 【新增】为复学申请补充新年级和新班级信息
     *
     * @param kjydjlbs 考籍异动记录的ID
     * @param dto      包含新年级和新班级的数据
     * @return 成功响应
     */
    @PutMapping("/transfers/{kjydjlbs}")
    @Operation(summary = "补充转学申请信息", description = "在审核前，由管理员为转学申请填入分配好的新班级。")
    public Result<Void> updateTransfersDetails(
            @PathVariable Long kjydjlbs,
            @Validated @RequestBody TransUpdateDTO dto) {

        // 调用Service层执行更新操作
        statusChangeService.updateTransDetails(kjydjlbs, dto);
        return Result.success("复学信息补充成功");
    }

    @GetMapping("/transfers/in-province-out")
    @Operation(summary = "查询【省内转出】申请列表")
    public Result<PageInfo<TransferAuditListVO>> listInProvinceOutTransfers(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.getTransfer(queryDTO, 1L));
    }

    @GetMapping("/transfers/in-province-in")
    @Operation(summary = "查询【省内转入】申请列表")
    public Result<PageInfo<TransferAuditListVO>> listInProvinceInTransfers(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.getTransfer(queryDTO, 2L));
    }

    @GetMapping("/transfers/out-of-province-out")
    @Operation(summary = "查询【转出到省外】申请列表")
    public Result<PageInfo<TransferAuditListVO>> listOutOfProvinceOutTransfers(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.getTransfer(queryDTO, 3L));
    }

    @GetMapping("/transfers/out-of-province-in")
    @Operation(summary = "查询【省外转入】申请列表")
    public Result<PageInfo<TransferAuditListVO>> listOutOfProvinceInTransfers(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.getTransfer(queryDTO, 4L));
    }

    @PostMapping("/transfers/in-province-out")
    @Operation(summary = "创建【省内转出】申请")
    public Result<Object> applyInProvinceOutTransfers(@RequestBody TransferApplyDetailDTO transferApplyDetailDTO) {
        statusChangeService.applyTransfer(transferApplyDetailDTO, 1L);
        return Result.success("省内转出申请提交成功");
    }

    @PostMapping("/transfers/in-province-in")
    @Operation(summary = "创建【省内转入】申请")
    public Result<Object> applyInProvinceInTransfers(@RequestBody TransferApplyDetailDTO transferApplyDetailDTO) {
        statusChangeService.applyTransfer(transferApplyDetailDTO, 2L);
        return Result.success("省内转入申请提交成功");
    }

    @PostMapping("/transfers/out-of-province-out")
    @Operation(summary = "创建【转出到省外】申请")
    public Result<Object> applyOutOfProvinceOutTransfers(@RequestBody TransferApplyDetailDTO transferApplyDetailDTO) {
        statusChangeService.applyTransfer(transferApplyDetailDTO, 3L);
        return Result.success("转出到省外申请提交成功");
    }

    @PostMapping("/transfers/out-of-province-in")
    @Operation(summary = "创建【省外转入】申请")
    public Result<Object> applyOutOfProvinceInTransfers(@RequestBody TransferApplyDetailDTO transferApplyDetailDTO) {
        statusChangeService.applyTransfer(transferApplyDetailDTO, 4L);
        return Result.success("省外转入申请提交成功");
    }

    // 流失相关
    @PostMapping("/attrition")
    @Operation(summary = "创建新的流失记录申请")
    public Result<Void> applyForAttrition(@Validated @RequestBody AttritionApplyDTO applyDTO) {
        statusChangeService.applyForAttrition(applyDTO);
        return Result.success("流失记录创建成功，请等待审核");
    }

    @GetMapping("/attrition")
    @Operation(summary = "查询流失记录申请列表")
    public Result<PageInfo<AttritionListVO>> listAttritionApplications(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.listAttritionApplications(queryDTO));
    }

    // 出国相关
    @PostMapping("/abroad")
    @Operation(summary = "创建新的出国登记记录")
    public Result<Void> applyForAbroad(@Validated @RequestBody AbroadApplyDTO applyDTO) {
        statusChangeService.applyForAbroad(applyDTO);
        return Result.success("出国登记成功");
    }

    @GetMapping("/abroad")
    @Operation(summary = "查询出国登记列表")
    public Result<PageInfo<AbroadListVO>> listAbroadApplications(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.listAbroadApplications(queryDTO));
    }

    // 关键信息变更相关
    @PostMapping("/key-property-change")
    @Operation(summary = "创建新的关键属性修改申请")
    public Result<Void> applyForKeyPropertyChange(@Validated @RequestBody KeyPropertyChangeApplyDTO applyDTO) {
        statusChangeService.applyForKeyPropertyChange(applyDTO);
        return Result.success("申请成功，请等待审核");
    }

    @GetMapping("/key-property-change")
    @Operation(summary = "查询关键属性修改申请列表")
    public Result<PageInfo<KeyPropertyChangeListVO>> listKeyPropertyChangeApps(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.listKeyPropertyChangeApps(queryDTO));
    }

    // 统计
    @GetMapping("/summary")
    @Operation(summary = "查询信息变更统计列表")
    public Result<PageInfo<InformationChangeSummaryVO>> listInformationChangeSummary(CommonQueryDTO queryDTO) {
        return Result.success(statusChangeService.listInformationChangeSummary(queryDTO));
    }

    // 统计按学校
    @GetMapping("/summary-by-school")
    @Operation(summary = "查询信息变更统计列表（按学校）")
    public Result<PageInfo<InformationChangeSummaryBySchoolVO>> listInformationChangeSummaryBySchool(SummaryQueryDTO queryDTO) {
        return Result.success(statusChangeService.listInformationChangeSummaryBySchool(queryDTO));
    }

    // 公共删除方法
    @DeleteMapping("/{kjydjlbs}")
    @Operation(summary = "删除学籍异动记录", description = "根据考籍异动记录标识ID删除对应的学籍异动记录")
    public Result<Void> deleteStatusChangeRecord(@PathVariable Long kjydjlbs) {
        String type = statusChangeService.deleteStatusChangeRecord(kjydjlbs);
        return Result.success(type + "记录删除成功");
    }

    // 审核相关接口
    @PostMapping("/{kjydjlbs}/audit")
    @Operation(summary = "审核学籍异动申请", description = "对指定ID的学籍异动申请进行审核（通过或驳回）")
    public Result<Void> audit(HttpServletRequest request,
                              @PathVariable Long kjydjlbs, @Validated @RequestBody AuditRequestDTO dto) {
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            System.out.println("Header: " + headerName + " = " + request.getHeader(headerName));
        });

        UserContext.UserInfo currentUser = UserContext.get();

        statusChangeService.auditApplication(kjydjlbs, dto, currentUser);
        return Result.success("审核操作成功");
    }
}
