package edu.qhjy.student.controller.admin;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.student.dto.classmanager.AssignStudentsDTO;
import edu.qhjy.student.dto.classmanager.ClassQueryDTO;
import edu.qhjy.student.dto.classmanager.ClassUpsertDTO;
import edu.qhjy.student.service.ClassManagerService;
import edu.qhjy.student.vo.ClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/student/avaliable")
    @Operation(summary = "查询可选择的学生", description = "通过班级id查询拥有的分页可选学生列表")
    public Result<?> getStudentForClassById(
            @RequestParam Long bjbs,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        var studentlist = classManagerService.getStudentAvailableByClassID(bjbs, pageNum, pageSize);

        return Result.success(studentlist);
    }

    @PostMapping("/assign-students")
    @Operation(summary = "批量分配学生到班级", description = "将传入的学生考号列表更新到指定班级")
    public Result<?> assignStudentsToClass(@RequestBody AssignStudentsDTO dto) {
        if (dto.getBjbs() == null || dto.getKshList() == null || dto.getKshList().isEmpty()) {
            return Result.error("班级ID或学生列表不能为空");
        }

        int updated = classManagerService.assignStudentsToClass(dto.getBjbs(), dto.getKshList());
        return Result.success(Map.of("msg", "成功更新 " + updated + " 个学生"));
    }

}