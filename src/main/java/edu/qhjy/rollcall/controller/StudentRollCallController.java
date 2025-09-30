package edu.qhjy.rollcall.controller;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.rollcall.dto.RollCallImportDTO;
import edu.qhjy.rollcall.dto.StudentRollCallExcelQueryDTO;
import edu.qhjy.rollcall.dto.StudentRollCallQueryDTO;
import edu.qhjy.rollcall.service.IStudentRollCallService;
import edu.qhjy.rollcall.vo.StudentRollCallListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roll-call")
@RequiredArgsConstructor
@Tag(name = "考勤管理", description = "学生点名相关接口")
public class StudentRollCallController {

    private final IStudentRollCallService studentRollCallService;

    @GetMapping("/list")
    @Operation(summary = "分页查询学生点名记录", description = "支持按学校、年级、班级、姓名、考号等条件查询")
    public Result<PageInfo<StudentRollCallListVO>> list(StudentRollCallQueryDTO query) {
        return Result.success(studentRollCallService.listStudents(query));
    }

    @GetMapping("/download")
    @Operation(summary = "下载点名册", description = "根据查询条件下载对应的点名册Excel文件")
    public void download(StudentRollCallExcelQueryDTO query, HttpServletResponse response) throws IOException {
        // 参数校验可以提前，如果失败就抛出自定义异常，让全局处理器去格式化响应
        if (query.getXxdm() == null || query.getXxdm().isEmpty()) {
            // 这种业务校验失败，最好是返回 400 Bad Request
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"code\":400,\"msg\":\"请选择一个学校\"}");
            return;
        }

        // 直接调用服务，不再需要 try-catch
        // 如果这里发生异常，会由上面的 GlobalExceptionHandler 统一处理
        studentRollCallService.downloadStudentList(query, response);
    }

    @PostMapping("/import")
    @Operation(summary = "导入点名记录 (JSON)", description = "批量导入学生点名记录")
    public Result<?> importRollCall(@RequestBody List<RollCallImportDTO> importList) {
        try {
            Map<String, Object> result = studentRollCallService.importRollCall(importList);
            return Result.success("导入成功", result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 新增: Excel 导入接口
     */
    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "导入点名记录 (Excel)", description = "上传填写好考勤信息的点名册Excel文件进行批量导入")
    public Result<?> importFromExcel(
            @Parameter(description = "Excel 文件", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return Result.error("上传失败，请选择文件");
        }
        try {
            Map<String, Object> result = studentRollCallService.importRollCallFromExcel(file);
            return Result.success("导入成功", result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

}