// 建议放置路径: edu.qhjy.common.web.GlobalExceptionHandler.java
package edu.qhjy.result;

import edu.qhjy.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 声明这是一个全局异常处理组件
public class GlobalExceptionHandler {

    /**
     * 专门捕获参数校验异常 (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 从异常中获取第一个校验失败的错误提示信息
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        // 返回一个 code 为 400 (Bad Request) 的错误响应
        return Result.error(400, message);
    }

    /**
     * 捕获非法参数异常 (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        // 返回一个 code 为 400 (Bad Request) 的错误响应
        return Result.error(400, e.getMessage());
    }

    /**
     * 捕获所有其他未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统出现未捕获的异常", e);
        // 返回一个通用的 500 (Internal Server Error) 错误响应
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}