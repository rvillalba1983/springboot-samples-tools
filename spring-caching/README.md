# spring-caching

Caché con [Caffeine](https://github.com/ben-manes/caffeine) usando **dos políticas de caducidad a la vez**.

## Cómo funciona

Spring Boot autoconfigura un único `CacheManager` (`spring.cache.caffeine.spec`). Cuando unos datos deben caducar en minutos y otros en días, se declaran varios. Es lo que hace `CacheManagersConfiguration`:

| Bean | Especificación | Uso |
|---|---|---|
| `cacheManagerHalfAnHour` (`@Primary`) | `app.cache.specs.half-an-hour` | Por defecto |
| `cacheManagerOneDay` | `app.cache.specs.one-day` | `@Cacheable(cacheManager = "cacheManagerOneDay")` |

```properties
app.cache.specs.half-an-hour=maximumSize=1500,expireAfterWrite=30m,recordStats
app.cache.specs.one-day=maximumSize=1500,expireAfterWrite=1d,recordStats
```

`CountryService` muestra:
- `@Cacheable` con el CacheManager por defecto y con uno concreto;
- `unless = "#result == null"` para no intentar guardar nulos, porque los CacheManager tienen `allowNullValues=false`;
- `@CacheEvict` para vaciar una caché.

Para desactivar la caché, por ejemplo al depurar: `spring.cache.type=none`.

## Tests

`CountryServiceTest` usa contadores de invocaciones para comprobar:
- que el método solo se ejecuta la primera vez;
- que cada caché vive en su CacheManager;
- que los nulos no se guardan;
- que el *evict* obliga a recalcular.
