# spring-boot-admin

Consola web de [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) para ver el estado de aplicaciones Spring Boot: salud, métricas, logs, variables de entorno, hilos...

## Cómo funciona

- **Servidor:** `@EnableAdminServer` en `SpringBootAdminApplication`. Escucha en el puerto 9089.
- **Cliente:** cada aplicación que se quiera monitorizar añade `spring-boot-admin-starter-client` y actuator, y apunta al servidor:

  ```properties
  spring.boot.admin.client.url=http://localhost:9089
  management.endpoints.web.exposure.include=*
  ```

  En este ejemplo el propio servidor se registra como cliente, así que aparece en la consola nada más arrancar.

Para avisar por correo cuando una aplicación se cae, hay que añadir `spring-boot-starter-mail` y configurar `spring.boot.admin.notify.mail.*` (ver `application.properties`).

> En producción, exponer todos los endpoints de Actuator no es buena idea: conviene limitarlos y proteger tanto la consola como los endpoints con Spring Security.

## Probar

```bash
mvn spring-boot:run
# abrir http://localhost:9089
```

## Tests

`SpringBootAdminApplicationTest` arranca el servidor y comprueba que responde la API de instancias y la salud de Actuator.
