package com.rvillalba.samples.caching;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Simula consultas costosas a un servicio externo. Los contadores permiten comprobar en los
 * tests cuántas veces se ejecuta realmente cada método.
 */
@Service
public class CountryService {

    private static final Map<String, String> CAPITALS = Map.of("ES", "Madrid", "FR", "París", "PT", "Lisboa");

    private final AtomicInteger capitalCalls = new AtomicInteger();
    private final AtomicInteger populationCalls = new AtomicInteger();

    /**
     * Dato que casi no cambia: caché de un día. Como el CacheManager no admite nulls,
     * {@code unless} evita intentar guardar un país desconocido.
     */
    @Cacheable(cacheNames = "capitals", cacheManager = CacheManagersConfiguration.ONE_DAY,
            unless = "#result == null")
    public String capitalOf(String countryCode) {
        capitalCalls.incrementAndGet();
        return CAPITALS.get(countryCode);
    }

    /** Dato que cambia más a menudo: usa el CacheManager por defecto (@Primary, 30 minutos). */
    @Cacheable("population")
    public long populationOf(String countryCode) {
        populationCalls.incrementAndGet();
        return 1_000_000L * countryCode.chars().sum();
    }

    @CacheEvict(cacheNames = "population", allEntries = true)
    public void refreshPopulation() {
        // Vacía la caché de población: la siguiente llamada volverá a calcularla
    }

    public int capitalCalls() {
        return capitalCalls.get();
    }

    public int populationCalls() {
        return populationCalls.get();
    }
}
