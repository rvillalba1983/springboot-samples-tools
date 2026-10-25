# spring-async

Ejecución asíncrona con `@Async` en un pool de hilos propio.

## Cómo funciona

- `@EnableAsync` activa el soporte.
- `AsyncExecutorConfiguration` declara un `ThreadPoolTaskExecutor` llamado `reportExecutor`, configurado desde `application.properties` mediante el record `AsyncExecutorProperties`:

  ```properties
  async.executor.core-pool-size=4
  async.executor.max-pool-size=8
  async.executor.queue-capacity=100
  async.executor.thread-name-prefix=report-
  ```

- `ReportService` usa `@Async("reportExecutor")` y devuelve un `CompletableFuture`. Quien lo llama sigue trabajando y recoge el resultado cuando lo necesita (`join()`, `thenApply()`...).

## Cuándo usar un pool propio

Spring Boot ya crea un executor por defecto (`applicationTaskExecutor`) que se ajusta con `spring.task.execution.pool.*`, y basta con `@Async` sin nombre. Un pool propio sirve para **aislar** tareas pesadas, como informes, integraciones lentas o envíos masivos, para que no se coman los hilos del resto.

Con Java 21+ también se puede activar `spring.threads.virtual.enabled=true`: el executor por defecto pasa a usar hilos virtuales, muy indicados para tareas que pasan la mayor parte del tiempo esperando E/S.

## Tests

`ReportServiceTest` comprueba que:
- el pool se configura desde las properties;
- las tareas se ejecutan en sus hilos;
- se pueden lanzar varias en paralelo;
- las excepciones llegan al llamante a través del `CompletableFuture`.
