package com.rvillalba.samples.async;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuración del pool de hilos dedicado, enlazada desde {@code async.executor.*}.
 */
@ConfigurationProperties("async.executor")
public record AsyncExecutorProperties(
        @DefaultValue("4") int corePoolSize,
        @DefaultValue("8") int maxPoolSize,
        @DefaultValue("100") int queueCapacity,
        @DefaultValue("async-") String threadNamePrefix) {
}
