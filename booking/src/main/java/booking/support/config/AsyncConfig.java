package booking.support.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Value("${spring.async.execution.pool.core-size}")
    int threadPoolCoreSize;

    @Value("${spring.async.execution.pool.max-size}")
    int threadPoolMaxSize;

    @Value("${spring.async.execution.pool.queue-capacity}")
    int threadPoolQueueCapacity;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolCoreSize);
        executor.setMaxPoolSize(threadPoolMaxSize);
        executor.setQueueCapacity(threadPoolQueueCapacity);
        executor.setThreadNamePrefix("Executor -- ");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                log.error("[ASYNC] Method: {}, Exception: {}", method.getName(), ex.getMessage());
            }
        };
    }
}
