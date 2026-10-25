# springboot-samples-tools

[![CI](https://github.com/rvillalba1983/springboot-samples-tools/actions/workflows/ci.yml/badge.svg)](https://github.com/rvillalba1983/springboot-samples-tools/actions/workflows/ci.yml)

Ejemplos pequeños e independientes de funcionalidades de Spring Boot que suelen hacer falta en proyectos reales. Cada módulo es una aplicación que se puede arrancar por separado y tiene sus propios tests.

**Stack:** Java 25 · Spring Boot 4.1 · Maven

## Módulos

| Módulo | Qué enseña |
|---|---|
| [spring-async](spring-async) | `@Async` con un `ThreadPoolTaskExecutor` propio configurado desde properties, `CompletableFuture` y propagación de excepciones |
| [spring-caching](spring-caching) | Caché con Caffeine y **dos CacheManager con distinta caducidad** a la vez, `@Cacheable`, `@CacheEvict`, nulos |
| [spring-internationalization](spring-internationalization) | Mensajes traducidos con `MessageSource`, parámetros y elección de idioma por `Accept-Language` |
| [spring-mail](spring-mail) | Envío de correo de texto y HTML con `JavaMailSender`, probado con un servidor SMTP en memoria (GreenMail) |
| [spring-boot-admin](spring-boot-admin) | Consola de [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) para monitorizar aplicaciones a través de Actuator |
| [spring-oauth2-authorization-server](spring-oauth2-authorization-server) | Servidor de autorización OAuth2 con Spring Authorization Server que emite tokens JWT |
| [spring-oauth2-resource-server](spring-oauth2-resource-server) | API protegida con esos tokens, con permisos por *scope* |

Los dos módulos OAuth2 funcionan juntos. La CI comprueba el flujo completo: arranca ambos, pide un token y llama a la API con él.

## Ejecutar

```bash
mvn verify                                    # compila y ejecuta los tests de todos los módulos
mvn -pl spring-caching spring-boot:run        # arranca un módulo concreto
```

## Historia y cambios de la versión 2.0 (2026)

Este repositorio nació en 2018 como una colección de librerías compartidas para microservicios con Spring Boot 1.5. En 2026 se ha reescrito para Spring Boot 4.1 y Java 25:

- **Cada módulo es ahora un ejemplo autocontenido.** Antes eran librerías que se combinaban a través de un módulo común de tests y de Spring Cloud Config.
- **`spring-authentication-parent` → `spring-oauth2-authorization-server` + `spring-oauth2-resource-server`.** El original usaba `spring-security-oauth2`, abandonado desde 2022, y el grant `password`, que eliminó OAuth 2.1. Se ha rehecho con Spring Authorization Server y el soporte de *resource server* de Spring Security, usando el grant `client_credentials`.
- **Se eliminan `spring-unit-test` y `spring-integration-test`.**
  - `spring-unit-test` reunía JUnit 4, JMockit, junit-dataprovider y anotaciones de Allure. Hoy esas necesidades las cubren JUnit 5 (con tests parametrizados), Mockito y AssertJ, que ya vienen con `spring-boot-starter-test`. El repositorio [mockito-and-more-sample](https://github.com/rvillalba1983/mockito-and-more-sample) tiene ejemplos actuales de testing.
  - `spring-integration-test` no tenía código, solo dependencias. Las clases base de contexto se sustituyen por las anotaciones de test de Spring Boot (`@SpringBootTest`, `@AutoConfigureMockMvc`, `MockMvcTester`...).
- **`spring-boot-admin-parent` (servidor + cliente) → `spring-boot-admin`.** Ahora es un único módulo: el servidor se registra a sí mismo como cliente.
- **Otros cambios:**
  - Se quitan Spring Cloud Config y `@RefreshScope`, que no hacían falta para los ejemplos.
  - Se usa el log por defecto (Logback) en vez de Log4j2.
  - La configuración se enlaza con *records* `@ConfigurationProperties`.
  - Paquetes `es.rvillalba.spring` → `com.rvillalba.samples`.
