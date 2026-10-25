# spring-oauth2-resource-server

API protegida con tokens JWT emitidos por [spring-oauth2-authorization-server](../spring-oauth2-authorization-server).

## Cómo funciona

- `spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9000` indica quién emite los tokens. Spring Security descarga de ahí las claves públicas y valida firma, caducidad y emisor de cada JWT.
- Cada *scope* del token se convierte en una authority `SCOPE_<scope>`. `SecurityConfiguration` las usa para dar permisos:

| Endpoint | Requiere |
|---|---|
| `GET /public/ping` | Nada |
| `GET /api/me` | `SCOPE_samples.read` |
| `POST /api/notes` | `SCOPE_samples.write` |

- La API no guarda sesión (`STATELESS`): cada petición trae su token en `Authorization: Bearer ...`.

## Probar con los dos módulos

```bash
# Terminal 1
mvn -pl spring-oauth2-authorization-server spring-boot:run
# Terminal 2
mvn -pl spring-oauth2-resource-server spring-boot:run
# Terminal 3
TOKEN=$(curl -s -u samples-client:samples-secret -d grant_type=client_credentials -d scope=samples.read \
        http://localhost:9000/oauth2/token | jq -r .access_token)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/me
```

## Tests

`SampleControllerTest` usa `jwt()` de spring-security-test para simular tokens con distintos scopes sin levantar el servidor de autorización. Cubre:
- el 401 sin token;
- el 403 con un scope insuficiente;
- la lectura con `samples.read`;
- la escritura con `samples.write`.

El flujo completo con tokens reales se prueba en la CI.
