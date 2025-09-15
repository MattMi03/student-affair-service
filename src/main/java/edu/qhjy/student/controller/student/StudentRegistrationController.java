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
@Tag(name = "学生注册接口", description = "提供学生查询、创建和更新个人注册信息的功能")
public class StudentRegistrationController {

    private final StudentRegistrationService registrationService;

    // 辅助方法，从请求头获取认证后的考生号
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

    @GetMapping
    @Operation(summary = "获取我的注册信息", description = "查询当前登录学生的注册信息")
    public Result<RegistrationInfoDTO> getMyRegistrationInfo(HttpServletRequest request, @RequestParam String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        return Result.success(registrationService.getRegistrationInfo(ksh));
    }

    @PostMapping
    @Operation(summary = "创建我的注册信息", description = "提交当前登录学生的注册信息进行创建")
    public Result<Void> createMyRegistration(@RequestBody RegistrationInfoDTO registrationInfo, HttpServletRequest request,
                                             @RequestParam String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        registrationService.createRegistrationByStudent(ksh, registrationInfo);
        return Result.success("注册信息创建成功");
    }

    @PutMapping
    @Operation(summary = "更新我的注册信息", description = "提交当前登录学生的注册信息进行更新")
    public Result<Void> updateMyRegistration(@RequestBody RegistrationInfoDTO registrationInfo, HttpServletRequest request,
                                             @RequestParam String getKsh) {
        String ksh = getCurrentUserKsh(request, getKsh);
        registrationService.updateRegistrationByStudent(ksh, registrationInfo);
        return Result.success("注册信息更新成功");
    }
}