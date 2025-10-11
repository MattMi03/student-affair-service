package edu.qhjy.qzsh.controller;

import com.github.pagehelper.PageInfo;
import edu.qhjy.common.Result;
import edu.qhjy.qzsh.domain.Qzsh;
import edu.qhjy.qzsh.dto.QzshQueryDTO;
import edu.qhjy.qzsh.dto.QzshUpsertDTO;
import edu.qhjy.qzsh.service.QzshService;
import edu.qhjy.qzsh.vo.QzshListVO;
import edu.qhjy.statuschange.dto.audit.AuditRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "高考资格前置审核管理")
@RestController
@RequestMapping("/api/admin/pre-audits")
@RequiredArgsConstructor
public class QzshController {

    private final QzshService qzshService;

    @Operation(summary = "分页查询前置审核列表")
    @GetMapping
    public Result<PageInfo<QzshListVO>> list(QzshQueryDTO query) {
        return Result.success(qzshService.listQzsh(query));
    }

    @Operation(summary = "根据考生标识(ksbs)获取详情")
    @GetMapping("/{ksbs}")
    public Result<Qzsh> getById(@PathVariable Long ksbs) {
        return Result.success(qzshService.getQzshById(ksbs));
    }

    @Operation(summary = "新增或修改前置审核信息")
    @PostMapping
    public Result<Qzsh> createOrUpdate(@Validated @RequestBody QzshUpsertDTO dto) {
        Qzsh result = qzshService.createOrUpdateQzsh(dto);
        return Result.success("操作成功", result);
    }

    @Operation(summary = "删除前置审核信息")
    @DeleteMapping("/{ksbs}")
    public Result<Void> delete(@PathVariable Long ksbs) {
        qzshService.deleteQzsh(ksbs);
        return Result.success("删除成功");
    }

    @Operation(summary = "批量删除前置审核信息")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        qzshService.deleteQzshBatch(ids);
        return Result.success("批量删除成功");
    }

    @PostMapping("/audit")
    @Operation(summary = "审核前置审核信息")
    public Result<Void> audit(@RequestParam Long ksbs, @Validated @RequestBody AuditRequestDTO auditRequestDTO) {
        qzshService.audit(ksbs, auditRequestDTO);
        return Result.success("审核成功");
    }
}