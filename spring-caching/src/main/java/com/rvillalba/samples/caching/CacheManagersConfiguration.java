package com.rvillalba.samples.caching;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Dos CacheManager de Caffeine con distinta caducidad. Spring Boot autoconfigura uno solo
 * ({@code spring.cache.caffeine.spec}); cuando hacen falta varias políticas a la vez se declaran
 * así y cada método elige el suyo con {@code @Cacheable(cacheManager = ...)}.
 */
@Configuration(proxyBeanMethods = false)
public class CacheManagersConfiguration {

    public static final String HALF_AN_HOUR = "cacheManagerHalfAnHour";
    public static final String ONE_DAY = "cacheManagerOneDay";

    @Primary
    @Bean(HALF_AN_HOUR)
    CacheManager cacheManagerHalfAnHour(CacheSpecsProperties specs) {
        return caffeineCacheManager(specs.halfAnHour());
    }

    @Bean(ONE_DAY)
    CacheManager cacheManagerOneDay(CacheSpecsProperties specs) {
        return caffeineCacheManager(specs.oneDay());
    }

    private static CaffeineCacheManager caffeineCacheManager(String spec) {
        var cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(false);
        cacheManager.setCacheSpecification(spec);
        return cacheManager;
    }
}
