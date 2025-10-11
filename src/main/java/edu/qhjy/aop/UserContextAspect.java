package edu.qhjy.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Aspect
@Component
public class UserContextAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    @Before("controllerPointcut()")
    public void beforeController() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-UserName");
        String realName = request.getHeader("X-User-RealName");
        if (realName != null) {
            realName = URLDecoder.decode(realName, StandardCharsets.UTF_8);
        }
        String userType = request.getHeader("X-User-Type");
        String js = request.getHeader("X-User-JS");
        String dm = request.getHeader("X-User-DM");

        UserContext.UserInfo userInfo = new UserContext.UserInfo(
                userId, username, realName, userType, js, dm
        );
        UserContext.set(userInfo);
    }

    @After("controllerPointcut()")
    public void afterController(JoinPoint joinPoint) {
        UserContext.clear();
    }
}