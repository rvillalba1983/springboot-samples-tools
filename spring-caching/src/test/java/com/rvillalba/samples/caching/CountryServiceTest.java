package com.rvillalba.samples.caching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CountryServiceTest {

    @Autowired
    private CountryService countryService;

    @Autowired
    @Qualifier(CacheManagersConfiguration.ONE_DAY)
    private CacheManager oneDayCacheManager;

    @Autowired
    @Qualifier(CacheManagersConfiguration.HALF_AN_HOUR)
    private CacheManager halfAnHourCacheManager;

    @Test
    void capitalIsCalculatedOnlyOncePerCountry() {
        countryService.capitalOf("ES");
        countryService.capitalOf("ES");
        countryService.capitalOf("ES");
        countryService.capitalOf("FR");

        assertThat(countryService.capitalOf("ES")).isEqualTo("Madrid");
        assertThat(countryService.capitalCalls()).isEqualTo(2);
    }

    @Test
    void eachMethodUsesItsOwnCacheManager() {
        countryService.capitalOf("PT");
        countryService.populationOf("PT");

        assertThat(oneDayCacheManager.getCacheNames()).containsExactly("capitals");
        assertThat(halfAnHourCacheManager.getCacheNames()).containsExactly("population");
        assertThat(oneDayCacheManager.getCache("capitals").get("PT", String.class)).isEqualTo("Lisboa");
    }

    @Test
    void nullValuesAreNotCached() {
        // Con allowNullValues=false, un null no se guarda y la siguiente llamada vuelve a ejecutarse
        countryService.capitalOf("XX");

        assertThat(oneDayCacheManager.getCache("capitals").get("XX")).isNull();
    }

    @Test
    void evictForcesRecalculation() {
        countryService.populationOf("ES");
        countryService.populationOf("ES");
        countryService.refreshPopulation();
        countryService.populationOf("ES");

        assertThat(countryService.populationCalls()).isEqualTo(2);
    }
}
