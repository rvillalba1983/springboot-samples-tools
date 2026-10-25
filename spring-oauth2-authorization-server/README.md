# spring-oauth2-authorization-server

Servidor de autorización OAuth2 con [Spring Authorization Server](https://docs.spring.io/spring-authorization-server/reference/). Emite tokens JWT firmados que valida el módulo [spring-oauth2-resource-server](../spring-oauth2-resource-server).

## Cómo funciona

No hay código de configuración: Spring Boot monta el servidor a partir de `application.properties`.

```properties
spring.security.oauth2.authorizationserver.client.samples-client.registration.client-id=samples-client
spring.security.oauth2.authorizationserver.client.samples-client.registration.client-secret={noop}samples-secret
spring.security.oauth2.authorizationserver.client.samples-client.registration.authorization-grant-types=client_credentials
spring.security.oauth2.authorizationserver.client.samples-client.registration.scopes=samples.read,samples.write
```

Endpoints principales:

| Endpoint | Para qué |
|---|---|
| `POST /oauth2/token` | Obtener un token |
| `GET /oauth2/jwks` | Claves públicas para validar los JWT |
| `GET /.well-known/oauth-authorization-server` | Metadatos del servidor (los usa el resource server) |

> **Cambio respecto a la versión original:** usaba `spring-security-oauth2`, abandonado, y el grant `password`, que OAuth 2.1 eliminó por inseguro. Para comunicación entre servicios se usa `client_credentials`. Para usuarios finales, lo recomendado es `authorization_code` con PKCE.

## Probar

```bash
mvn spring-boot:run
curl -u samples-client:samples-secret -d grant_type=client_credentials -d scope=samples.read \
     http://localhost:9000/oauth2/token
```

```json
{"access_token":"eyJraWQiOi...","scope":"samples.read","token_type":"Bearer","expires_in":599}
```

## Tests

`AuthorizationServerTest` comprueba:
- que se emite el token con credenciales correctas;
- el 401 con un secreto erróneo;
- el error `invalid_scope` con un scope no permitido;
- que se publican los metadatos y las claves públicas.
