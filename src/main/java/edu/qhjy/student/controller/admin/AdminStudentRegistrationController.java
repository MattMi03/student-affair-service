package edu.qhjy.student.controller.admin;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.student.dto.registeration.AdminStatisticsQueryDTO;
import edu.qhjy.student.dto.registeration.AdminStudentQueryDTO;
import edu.qhjy.student.dto.registeration.AuditRequestDTO;
import edu.qhjy.student.dto.registeration.RegistrationInfoDTO;
import edu.qhjy.student.service.StudentRegistrationService;
import edu.qhjy.student.vo.ImportResultVO;
import edu.qhjy.student.vo.StudentListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/admin/registrations")
@RequiredArgsConstructor
@Tag(name = "管理员学生注册接口", description = "提供学生注册信息的管理功能，包括查询、创建、更新和删除学生注册信息")
public class AdminStudentRegistrationController {

    private final StudentRegistrationService registrationService;

    @GetMapping
    @Operation(summary = "分页查询学生列表", description = "根据查询条件分页获取学生注册信息")
    public Result<PageInfo<StudentListVO>> listStudents(AdminStudentQueryDTO queryDTO) {
        return Result.success(registrationService.listStudentsByPage(queryDTO));
    }

    @GetMapping("/{ksh}")
    @Operation(summary = "根据考生号获取学生详情", description = "通过考生号查询学生的注册详细信息")
    public Result<RegistrationInfoDTO> getStudentDetails(@PathVariable String ksh) {
        return Result.success(registrationService.getRegistrationInfo(ksh));
    }

    @PostMapping
    @Operation(summary = "创建新学生注册信息", description = "提交学生注册信息进行创建")
    public Result<Void> createStudent(@RequestBody RegistrationInfoDTO registrationInfo) {
        registrationService.createRegistrationByAdmin(registrationInfo);
        return Result.success("创建成功");
    }

    @PutMapping("/{ksh}")
    @Operation(summary = "更新学生注册信息", description = "通过考生号更新学生的注册信息")
    public Result<Void> updateStudent(@PathVariable String ksh, @RequestBody RegistrationInfoDTO registrationInfo) {
        registrationService.updateRegistrationByAdmin(ksh, registrationInfo);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{ksh}")
    @Operation(summary = "删除学生注册信息", description = "通过考生号删除学生的注册信息")
    public Result<Void> deleteStudent(@PathVariable String ksh) {
        registrationService.deleteRegistrationByAdmin(ksh);
        return Result.success("删除成功");
    }

    @PostMapping("/audits/{ksh}")
    @Operation(summary = "审核学生注册信息", description = "通过考生号审核学生的注册信息")
    public Result<Void> auditStudent(@Validated @RequestBody AuditRequestDTO auditRequest, @PathVariable String ksh) {
        System.out.println(auditRequest);
        registrationService.auditRegistration(ksh, auditRequest);
        return Result.success("审核成功");
    }

    // 统计学生
    @GetMapping("/statistics")
    @Operation(summary = "统计学生注册信息", description = "获取学生注册信息的统计数据")
    public Result<?> statistics(AdminStatisticsQueryDTO queryDTO) {
        return Result.success(registrationService.statistics(queryDTO));
    }

    // 在您的学生注册相关的Controller中，例如 StudentRegistrationController.java

    @Operation(summary = "下载学生信息批量导入模板")
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] excelContent = registrationService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename("学生信息批量导入模板.xlsx", StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    @Operation(summary = "通过Excel批量导入学生信息")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ImportResultVO> importStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        try {
            ImportResultVO result = registrationService.importFromExcel(file);
            return Result.success("导入处理完成", result);
        } catch (IOException e) {
            log.error("文件导入失败", e);
            return Result.error("文件读取失败: " + e.getMessage());
        }
    }
}