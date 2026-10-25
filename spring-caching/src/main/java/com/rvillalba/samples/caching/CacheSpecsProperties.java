package com.rvillalba.samples.caching;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Especificaciones de Caffeine (formato {@code CaffeineSpec}) para cada CacheManager.
 *
 * @param halfAnHour caché de caducidad corta (30 minutos)
 * @param oneDay     caché de caducidad larga (1 día)
 */
@ConfigurationProperties("app.cache.specs")
public record CacheSpecsProperties(String halfAnHour, String oneDay) {
}
