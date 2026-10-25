package com.rvillalba.samples.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Pool de hilos propio para tareas pesadas, separado del executor por defecto de Spring Boot
 * ({@code applicationTaskExecutor}, configurable con {@code spring.task.execution.*}).
 * Se usa indicando su nombre: {@code @Async(AsyncExecutorConfiguration.REPORT_EXECUTOR)}.
 */
@Configuration(proxyBeanMethods = false)
public class AsyncExecutorConfiguration {

    public static final String REPORT_EXECUTOR = "reportExecutor";

    @Bean(name = REPORT_EXECUTOR)
    ThreadPoolTaskExecutor reportExecutor(AsyncExecutorProperties properties) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.corePoolSize());
        executor.setMaxPoolSize(properties.maxPoolSize());
        executor.setQueueCapacity(properties.queueCapacity());
        executor.setThreadNamePrefix(properties.threadNamePrefix());
        // Al parar la aplicación espera a que terminen las tareas en curso
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }
}
