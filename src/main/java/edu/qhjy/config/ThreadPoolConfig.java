package edu.qhjy.config;// 在您的配置类中 (例如 ScoreApplication.java 或一个专门的 @Configuration 类)

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean("ioTaskExecutor")
    public Executor ioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：根据您服务器的CPU核心数来定，例如8核CPU可以设置为8
        executor.setCorePoolSize(8);
        // 最大线程数
        executor.setMaxPoolSize(16);
        // 队列容量：当所有线程都在忙时，新任务的等待队列大小
        executor.setQueueCapacity(100);
        // 线程名称前缀
        executor.setThreadNamePrefix("Preload-Executor-");
        // 拒绝策略：当队列已满且线程数达到最大时，由调用者线程（主线程）自己执行该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}