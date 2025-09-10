package edu.qhjy.aop;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 确保这是第一个执行的 Filter
public class PerformanceLoggingFilter implements Filter {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PerformanceLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = (System.nanoTime() - startTime) / 1_000_000; // 转换为毫秒
            HttpServletRequest req = (HttpServletRequest) request;
            log.debug("请求 [{} {}] 的完整处理耗时: {}ms", req.getMethod(), req.getRequestURI(), duration);
        }
    }
}