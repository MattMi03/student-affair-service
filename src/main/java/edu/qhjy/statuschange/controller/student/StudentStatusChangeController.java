// 文件路径: src/main/java/edu/qhjy/statuschange/controller/student/StudentStatusChangeController.java
package edu.qhjy.statuschange.controller.student;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.statuschange.dto.*;
import edu.qhjy.statuschange.service.StatusChangeService;
import edu.qhjy.statuschange.vo.KeyPropertyChangeListVO;
import edu.qhjy.statuschange.vo.LeaveAuditListVO;
import edu.qhjy.statuschange.vo.ReturnAuditListVO;
import edu.qhjy.statuschange.vo.TransferAuditListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/status-changes")
@AllArgsConstructor
@Tag(name = "学生端考籍异动接口", description = "提供学生对自己考籍进行异动申请和查询的功能")
public class StudentStatusChangeController {

    private final StatusChangeService statusChangeService;

    /**
     * 辅助方法：获取当前学生的考生号。
     * 优先从请求头(模拟安全认证)获取，如果获取不到，则回退到使用URL参数(用于临时测试)。
     *
     * @param request HttpServletRequest
     * @param getKsh  从 @RequestParam 传入的 ksh
     * @return 最终确定的考生号
     */
    private String getCurrentUserKsh(HttpServletRequest request, String getKsh) {
        // 生产环境中，应优先并强制使用从安全上下文获取的用户信息
        String kshFromHeader = request.getHeader("x-user-username");
        if (kshFromHeader != null && !kshFromHeader.isEmpty()) {
            return kshFromHeader;
        }

        // 仅在请求头中没有用户信息时，才使用URL参数作为回退（用于测试）
        if (getKsh != null && !getKsh.isEmpty()) {
            return getKsh;
        }

        throw new RuntimeException("无法获取考生号(ksh)，请确保已登录或在测试时提供了ksh参数");
    }

    // --- 休学相关接口 ---

    @PostMapping("/suspension")
    @Operation(summary = "学生申请休学", description = "提交休学申请")
    public Result<Void> applyForSuspension(
            @Validated @RequestBody LeaveApplyDetailDTO leaveApplyDetailDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        // 强制将DTO中的ksh设置为最终确定的考生号，确保安全性
        leaveApplyDetailDTO.getStudentBasicInfoDTO().setKsh(ksh);

        statusChangeService.createSuspension(leaveApplyDetailDTO);
        return Result.success("休学申请成功");
    }

    @GetMapping("/suspension")
    @Operation(summary = "学生查询自己的休学申请记录", description = "获取当前学生的所有休学申请")
    public Result<PageInfo<LeaveAuditListVO>> getMySuspensions(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        // 强制将查询条件中的ksh设置为当前登录的学生，防止越权查询
        CommonQueryDTO commonQueryDTO = new CommonQueryDTO();
        commonQueryDTO.setKsh(ksh);

        PageInfo<LeaveAuditListVO> leaveAuditListVO = statusChangeService.getSuspension(commonQueryDTO);
        return Result.success(leaveAuditListVO);
    }

    // --- 复学相关接口 ---
    @PostMapping("/return-school")
    @Operation(summary = "学生创建新的复学申请")
    public Result<Void> applyForReturn(
            @Validated @RequestBody ReturnApplyDTO applyDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        applyDTO.getStudentBasicInfoDTO().setKsh(ksh);
        statusChangeService.applyForReturn(applyDTO);
        return Result.success("复学申请提交成功");
    }

    @GetMapping("/return-school")
    @Operation(summary = "学生查询复学申请")
    public Result<PageInfo<ReturnAuditListVO>> listReturnApplications(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        CommonQueryDTO queryDTO = new CommonQueryDTO();
        queryDTO.setKsh(ksh);
        return Result.success(statusChangeService.listReturnApplications(queryDTO));
    }

    // --- 转学相关接口 ---

    @PostMapping("/transfers/in-province-out")
    @Operation(summary = "学生申请省内转出")
    public Result<Void> applyForInProvinceOut(
            @Validated @RequestBody TransferApplyDetailDTO transferApplyDetailDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        transferApplyDetailDTO.getStudentBasicInfoDTO().setKsh(ksh);

        statusChangeService.applyTransfer(transferApplyDetailDTO, 1L);
        return Result.success("省内转出申请成功");
    }

    @GetMapping("/transfers/in-province-out")
    @Operation(summary = "学生查询省内转出申请记录")
    public Result<PageInfo<TransferAuditListVO>> listInProvinceOutTransfers(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        CommonQueryDTO queryDTO = new CommonQueryDTO();
        queryDTO.setKsh(ksh);
        PageInfo<TransferAuditListVO> transferAuditList = statusChangeService.getTransfer(queryDTO, 1L);
        return Result.success(transferAuditList);
    }

    @PostMapping("/transfers/in-province-in")
    @Operation(summary = "学生申请省内转入")
    public Result<Void> applyForInProvinceIn(
            @Validated @RequestBody TransferApplyDetailDTO transferApplyDetailDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        transferApplyDetailDTO.getStudentBasicInfoDTO().setKsh(ksh);

        statusChangeService.applyTransfer(transferApplyDetailDTO, 2L);
        return Result.success("省内转入申请成功");
    }

    @GetMapping("/transfers/in-province-in")
    @Operation(summary = "学生查询省内转入申请记录")
    public Result<PageInfo<TransferAuditListVO>> listInProvinceInTransfers(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        CommonQueryDTO queryDTO = new CommonQueryDTO();
        queryDTO.setKsh(ksh);
        PageInfo<TransferAuditListVO> transferAuditList = statusChangeService.getTransfer(queryDTO, 2L);
        return Result.success(transferAuditList);
    }

    @PostMapping("/transfers/out-of-province-out")
    @Operation(summary = "学生申请转出到省外")
    public Result<Void> applyForOutOfProvinceOut(
            @Validated @RequestBody TransferApplyDetailDTO transferApplyDetailDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        transferApplyDetailDTO.getStudentBasicInfoDTO().setKsh(ksh);

        statusChangeService.applyTransfer(transferApplyDetailDTO, 3L);
        return Result.success("转出到省外申请成功");
    }

    @GetMapping("/transfers/out-of-province-out")
    @Operation(summary = "学生查询转出到省外申请记录")
    public Result<PageInfo<TransferAuditListVO>> listOutOfProvinceOutTransfers(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        CommonQueryDTO queryDTO = new CommonQueryDTO();
        queryDTO.setKsh(ksh);
        PageInfo<TransferAuditListVO> transferAuditList = statusChangeService.getTransfer(queryDTO, 3L);
        return Result.success(transferAuditList);
    }

    @PostMapping("/transfers/out-of-province-in")
    @Operation(summary = "学生申请省外转入")
    public Result<Void> applyForOutOfProvinceIn(
            @Validated @RequestBody TransferApplyDetailDTO transferApplyDetailDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        transferApplyDetailDTO.getStudentBasicInfoDTO().setKsh(ksh);

        statusChangeService.applyTransfer(transferApplyDetailDTO, 4L);
        return Result.success("省外转入申请成功");
    }

    @GetMapping("/transfers/out-of-province-in")
    @Operation(summary = "学生查询省外转入申请记录")
    public Result<PageInfo<TransferAuditListVO>> listOutOfProvinceInTransfers(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        CommonQueryDTO queryDTO = new CommonQueryDTO();
        queryDTO.setKsh(ksh);
        PageInfo<TransferAuditListVO> transferAuditList = statusChangeService.getTransfer(queryDTO, 4L);
        return Result.success(transferAuditList);
    }

    @GetMapping("/basic-info")
    @Operation(summary = "获取学生基本信息", description = "根据考生号获取学生的基本信息")
    public Result<?> getStudentBasicInfo(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String ksh) {
        // 强制使用请求头中的考生号，确保安全性
        ksh = getCurrentUserKsh(request, ksh);
        BasicInfoQueryDTO basicInfoQueryDTO = new BasicInfoQueryDTO();
        basicInfoQueryDTO.setKsh(ksh);
        var studentBasicInfo = statusChangeService.getBasicInfo(basicInfoQueryDTO);
        if (studentBasicInfo == null) {
            return Result.error("未找到学生信息");
        }
        return Result.success(studentBasicInfo);
    }

    @PostMapping("/key-property-change")
    @Operation(summary = "创建新的关键属性修改申请")
    public Result<Void> applyForKeyPropertyChange(
            @Validated @RequestBody KeyPropertyChangeApplyDTO applyDTO,
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        // 强制将DTO中的ksh设置为最终确定的考生号，确保
        applyDTO.getStudentBasicInfoDTO().setKsh(ksh);
        statusChangeService.applyForKeyPropertyChange(applyDTO);
        return Result.success("申请成功，请等待审核");
    }

    @GetMapping("/key-property-change")
    @Operation(summary = "查询关键属性修改申请列表")
    public Result<PageInfo<KeyPropertyChangeListVO>> listKeyPropertyChangeApps(
            HttpServletRequest request,
            @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        CommonQueryDTO queryDTO = new CommonQueryDTO();
        queryDTO.setKsh(ksh);
        return Result.success(statusChangeService.listKeyPropertyChangeApps(queryDTO));
    }

    @DeleteMapping("/{kjydjlbs}")
    @Operation(summary = "删除学籍异动记录", description = "根据考籍异动记录标识ID删除对应的学籍异动记录")
    public Result<Void> deleteStatusChangeRecord(@PathVariable Long kjydjlbs,
                                                 HttpServletRequest request,
                                                 @Parameter(description = "【临时测试用】考生号") @RequestParam(required = false) String getKsh) {

        String ksh = getCurrentUserKsh(request, getKsh);
        String type = statusChangeService.deleteStatusChangeRecord(kjydjlbs);
        return Result.success(type + "记录删除成功");
    }
}