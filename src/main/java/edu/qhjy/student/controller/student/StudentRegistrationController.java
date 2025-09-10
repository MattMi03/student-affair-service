package edu.qhjy.student.controller.student;

import edu.qhjy.common.Result;
import edu.qhjy.student.dto.registeration.RegistrationInfoDTO;
import edu.qhjy.student.service.StudentRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/registration") // 路径调整为更具体
@RequiredArgsConstructor
@Tag(name = "学生报名接口", description = "提供学生查询、创建和更新个人报名信息的功能")
public class StudentRegistrationController {

    private final StudentRegistrationService registrationService;

    // 辅助方法，从请求头获取认证后的考生号
    private String getCurrentKsh(HttpServletRequest request) {
        return request.getHeader("x-user-username");
    }

    @GetMapping
    @Operation(summary = "获取我的报名信息", description = "查询当前登录学生的报名信息")
    public Result<RegistrationInfoDTO> getMyRegistrationInfo(HttpServletRequest request, @RequestParam String getKsh) {
        String ksh = getCurrentKsh(request);
        if (getKsh != null && !getKsh.isEmpty()) {
            ksh = getKsh; // 如果有传入的考生号，则使用它
        }
        return Result.success(registrationService.getRegistrationInfo(ksh));
    }

    @PostMapping
    @Operation(summary = "创建我的报名信息", description = "提交当前登录学生的报名信息进行创建")
    public Result<Void> createMyRegistration(@RequestBody RegistrationInfoDTO registrationInfo, HttpServletRequest request,
                                             @RequestParam String getKsh) {
        String ksh = getCurrentKsh(request);
        if (getKsh != null && !getKsh.isEmpty()) {
            ksh = getKsh; // 如果有传入的考生号，则使用它
        }
        registrationService.createRegistrationByStudent(ksh, registrationInfo);
        return Result.success("报名信息创建成功");
    }

    @PutMapping
    @Operation(summary = "更新我的报名信息", description = "提交当前登录学生的报名信息进行更新")
    public Result<Void> updateMyRegistration(@RequestBody RegistrationInfoDTO registrationInfo, HttpServletRequest request,
                                             @RequestParam String getKsh) {
        String ksh = getCurrentKsh(request);
        if (getKsh != null && !getKsh.isEmpty()) {
            ksh = getKsh; // 如果有传入的考生号，则使用它
        }
        registrationService.updateRegistrationByStudent(ksh, registrationInfo);
        return Result.success("报名信息更新成功");
    }
}