package edu.qhjy.statuschange.controller.admin;

import com.alibaba.nacos.api.model.v2.Result;
import edu.qhjy.statuschange.service.StatusChangeService;
import edu.qhjy.statuschange.vo.InformationChangeVO;
import edu.qhjy.statuschange.vo.StatusChangeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/student-detail")
@RequiredArgsConstructor
@Tag(name = "管理端学生详情查询", description = "提供查询学生个人各类详细信息的接口")
public class StudentDetailController {

    private final StatusChangeService statusChangeService;

    @GetMapping("/{ksh}/information-changes")
    @Operation(summary = "获取学生信息变更列表", description = "根据考生号(ksh)查询该生的所有关键信息变更记录。")
    public Result<List<InformationChangeVO>> getInformationChanges(@PathVariable String ksh) {
        List<InformationChangeVO> list = statusChangeService.getInformationChangeListByKsh(ksh);
        return Result.success(list);
    }

    @GetMapping("/{ksh}/status-changes")
    @Operation(summary = "获取学生考籍异动列表", description = "根据考生号(ksh)查询该生的所有考籍异动记录，如休学、复学、转学等。")
    public Result<List<StatusChangeVO>> getStatusChanges(@PathVariable String ksh) {
        List<StatusChangeVO> list = statusChangeService.getStatusChangeListByKsh(ksh);
        return Result.success(list);
    }
}