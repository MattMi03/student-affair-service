// 建议放置路径: edu.qhjy.common.web.GlobalExceptionHandler.java
package edu.qhjy.result;

import edu.qhjy.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 专门捕获参数校验异常 (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletResponse response) {
        if (response.isCommitted()) {
            log.warn("响应已提交，无法返回 JSON 错误信息: {}", e.getMessage());
            return null;
        }
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 捕获非法参数异常 (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) {
        if (response.isCommitted()) {
            log.warn("响应已提交，无法返回 JSON 错误信息: {}", e.getMessage());
            return null;
        }
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 捕获非法状态异常 (IllegalStateException)
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<Void> handleIllegalStateException(IllegalStateException e, HttpServletResponse response) {
        if (response.isCommitted()) {
            log.warn("响应已提交，无法返回 JSON 错误信息: {}", e.getMessage());
            return null;
        }
        log.warn("非法状态异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 捕获未找到元素异常 (NoSuchElementException)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public Result<Void> handleNoSuchElementException(NoSuchElementException e, HttpServletResponse response) {
        if (response.isCommitted()) {
            log.warn("响应已提交，无法返回 JSON 错误信息: {}", e.getMessage());
            return null;
        }
        log.warn("未找到元素异常: {}", e.getMessage());
        return Result.error(404, e.getMessage());
    }

    /**
     * 捕获所有其他未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletResponse response) {
        // 关键：检查响应是否已经提交
        if (response.isCommitted()) {
            // 如果响应已提交，说明是文件下载等场景中发生的异常
            // 此时无法再向客户端发送错误信息，只能在服务器端记录日志
            log.error("Response has been committed. Unable to send error message to client. Error details: ", e);
        } else {
            // 响应未提交，可以安全地返回JSON错误信息
            log.error("An unexpected error occurred: ", e);
            response.reset(); // 清除任何可能已设置的头部信息
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                // 返回一个标准的错误结构
                String jsonError = "{\"code\":500,\"msg\":\"服务器内部错误，请联系管理员！\"}";
                response.getWriter().write(jsonError);
            } catch (IOException ioException) {
                log.error("Failed to write error response.", ioException);
            }
        }
    }
}