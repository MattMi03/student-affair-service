package edu.qhjy.statuschange.controller.admin;

import com.github.pagehelper.PageInfo;
import edu.qhjy.aop.UserContext;
import edu.qhjy.common.Result;
import edu.qhjy.statuschange.domain.Ydjb;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import edu.qhjy.statuschange.dto.remoteclass.AssignStudentsDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbQueryDTO;
import edu.qhjy.statuschange.dto.remoteclass.YdjbStudentQueryDTO;
import edu.qhjy.statuschange.service.YdjbService;
import edu.qhjy.statuschange.vo.remoteclass.YdjbListVO;
import edu.qhjy.statuschange.vo.remoteclass.YdjbStudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "异地集体办班管理")
@RestController
@RequestMapping("/api/admin/remote-classes")
@RequiredArgsConstructor
public class YdjbController {

    private final YdjbService ydjbService;

    @Operation(summary = "分页查询省外异地办班列表")
    @GetMapping("/out-of-province")
    public Result<PageInfo<YdjbListVO>> listOutOfProvince(YdjbQueryDTO query,
                                                          @RequestParam(defaultValue = "1") int pageNum,
                                                          @RequestParam(defaultValue = "10") int pageSize) {
        // bType=1 代表省外
        query.setBType(1);
        return Result.success(ydjbService.listYdjb(query, pageNum, pageSize));
    }

    @Operation(summary = "分页查询省内异地办班列表")
    @GetMapping("/in-province")
    public Result<PageInfo<YdjbListVO>> listInProvince(YdjbQueryDTO query,
                                                       @RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        // bType=2 代表省内
        query.setBType(2);
        return Result.success(ydjbService.listYdjb(query, pageNum, pageSize));
    }

    @Operation(summary = "新增省外异地办班申请")
    @PostMapping("/out-of-province")
    public Result<Ydjb> createOutOfProvince(@Validated @RequestBody YdjbDTO dto) {
        String creatorName = UserContext.get() != null ? UserContext.get().getRealName() : "System";
        Ydjb result = ydjbService.createYdjb(dto, 1, creatorName);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "新增省内异地办班申请")
    @PostMapping("/in-province")
    public Result<Ydjb> createInProvince(@Validated @RequestBody YdjbDTO dto) {
        String creatorName = UserContext.get() != null ? UserContext.get().getRealName() : "System";
        Ydjb result = ydjbService.createYdjb(dto, 2, creatorName);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改异地办班申请")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody YdjbDTO dto) {
        ydjbService.updateYdjb(id, dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除异地办班申请")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ydjbService.deleteYdjb(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "批量删除异地办班申请")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        ydjbService.deleteYdjbBatch(ids);
        return Result.success("批量删除成功");
    }

    @Operation(summary = "资料录入-查询学生列表")
    @GetMapping("/{id}/students")
    public Result<PageInfo<YdjbStudentVO>> listStudents(
            @PathVariable Long id,
            YdjbStudentQueryDTO query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(ydjbService.listStudents(id, query, pageNum, pageSize));
    }

    @Operation(summary = "资料录入-新增/导入学生")
    @PostMapping("/{id}/students")
    public Result<Void> addStudents(@PathVariable Long id, @RequestBody AssignStudentsDTO dto) {
        ydjbService.addStudents(id, dto);
        return Result.success("添加成功");
    }

    @Operation(summary = "资料录入-移除学生")
    @DeleteMapping("/{id}/students")
    public Result<Void> removeStudents(@PathVariable Long id, @RequestBody AssignStudentsDTO dto) {
        ydjbService.removeStudents(id, dto);
        return Result.success("移除成功");
    }

    @Operation(summary = "审核异地办班申请")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @Validated @RequestBody AuditRequestDTO dto) {

        ydjbService.auditYdjb(id, dto);
        return Result.success("审核操作成功");
    }
}