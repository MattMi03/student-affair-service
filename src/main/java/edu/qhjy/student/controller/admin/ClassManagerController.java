package edu.qhjy.student.controller.admin;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.student.dto.classmanager.AssignStudentsDTO;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.ClassUpsertDTO;
import edu.qhjy.student.dto.classmanager.StudentAvailableQueryDTO;
import edu.qhjy.student.service.ClassManagerService;
import edu.qhjy.student.vo.ClassVO;
import edu.qhjy.student.vo.ImportResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/admin/classes")
@Tag(name = "班级管理接口", description = "提供班级的增删改查功能")
public class ClassManagerController {

    @Autowired
    private ClassManagerService classManagerService;

    @GetMapping
    @Operation(summary = "分页查询班级列表", description = "根据查询条件分页获取班级信息")
    public Result<PageInfo<ClassVO>> list(ClassQueryDTO queryDTO) {
        PageInfo<ClassVO> pageData = classManagerService.listClassesByPage(queryDTO);
        return Result.success(pageData);
    }

    @GetMapping("/{bjbs}")
    @Operation(summary = "根据ID获取班级信息", description = "通过班级ID查询班级详细信息")
    public Result<ClassVO> getById(@PathVariable("bjbs") Long bjbs) {
        ClassVO classVO = classManagerService.getClassById(bjbs);
        return Result.success(classVO);
    }

    @PostMapping
    @Operation(summary = "创建新班级", description = "提交班级信息进行创建")
    public Result<Void> create(@Validated @RequestBody ClassUpsertDTO upsertDTO) {
        System.out.println("Received class upsert DTO: " + upsertDTO);
        boolean success = classManagerService.createClass(upsertDTO);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/{bjbs}")
    @Operation(summary = "更新班级信息", description = "提交班级信息进行更新")
    public Result<Void> update(@PathVariable("bjbs") Long bjbs, @Validated @RequestBody ClassUpsertDTO upsertDTO) {
        boolean success = classManagerService.updateClass(upsertDTO, bjbs);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{bjbs}")
    @Operation(summary = "删除班级", description = "通过班级ID删除班级信息")
    public Result<Void> delete(@PathVariable("bjbs") Long bjbs) {
        boolean success = classManagerService.deleteClass(bjbs);
        return success ? Result.success() : Result.error("删除失败");
    }

    @GetMapping("/student")
    @Operation(summary = "查询班级拥有学生", description = "通过班级id查询拥有的分页学生列表")
    public Result<?> getStudentById(
            @RequestParam Long bjbs,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        var studentlist = classManagerService.getStudentByClassID(bjbs, pageNum, pageSize);

        return Result.success(studentlist);
    }

    @Operation(summary = "查询可选择的学生", description = "通过班级id和可选的筛选条件查询拥有的分页可选学生列表")
    @GetMapping("/student/avaliable")
    public Result<?> getStudentForClassById(@Validated StudentAvailableQueryDTO query) {
        // Service方法现在接收整个DTO对象
        var studentlist = classManagerService.getStudentAvailableByClassID(query);
        return Result.success(studentlist);
    }

    @PostMapping("/assign-students")
    @Operation(summary = "批量分配学生到班级", description = "将传入的学生考号列表更新到指定班级")
    public Result<?> assignStudentsToClass(@RequestBody AssignStudentsDTO dto) {
        if (dto.getBjbs() == null || dto.getKshList() == null || dto.getKshList().isEmpty()) {
            return Result.error("班级ID或学生列表不能为空");
        }

        try {
            ImportResultVO result = classManagerService.assignStudentsToClass(dto.getBjbs(), dto.getKshList());
            return Result.success("分配完成", result);
        } catch (Exception e) {
            return Result.error("分配失败: " + e.getMessage());
        }
    }

    @Operation(summary = "下载分班Excel模板（预填充数据）")
    @GetMapping("/template/class-assignment")
    public ResponseEntity<byte[]> downloadClassAssignmentTemplate(
            @RequestParam(required = false) String xxdm,
            @RequestParam Integer jb) {

        // 1. 查询学校名称
        String schoolName = classManagerService.getSchoolNameByCode(xxdm);
        if (!StringUtils.hasText(schoolName)) {
            schoolName = "学校";
        }

        // 2. 生成 Excel
        byte[] excelContent = classManagerService.generateAssignStudentTemplate(xxdm, jb);

        // 3. 清理学校名称，防止 XSS/特殊字符
        String safeSchoolName = schoolName.replaceAll("[^\\w\\u4e00-\\u9fa5]", ""); // 允许中文、字母、数字
        String fileName = String.format("%s-%d级-分班模板.xlsx", safeSchoolName, jb);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }

    /**
     * [REFACTORED] 通过Excel批量更新学生的分班信息
     */
    @Operation(summary = "通过Excel批量分班")
    @PostMapping(value = "/assign-students/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ImportResultVO> importClassAssignments(
            @RequestParam String xxdm, @RequestParam("file") MultipartFile file) {
        try {
            ImportResultVO result = classManagerService.importStudentAssignments(xxdm, file);
            return Result.success("分班导入处理完成", result);
        } catch (IOException e) {
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "移除班级中的学生", description = "将指定学生的班级置空")
    @PostMapping("/remove-students")
    public Result<?> removeStudentsFromClass(@RequestBody List<String> kshList) {
        if (kshList == null || kshList.isEmpty()) {
            return Result.error("学生列表不能为空");
        }

        try {
            classManagerService.removeStudentsFromClass(kshList);
            return Result.success("移除成功");
        } catch (Exception e) {
            return Result.error("移除失败: " + e.getMessage());
        }
    }
}