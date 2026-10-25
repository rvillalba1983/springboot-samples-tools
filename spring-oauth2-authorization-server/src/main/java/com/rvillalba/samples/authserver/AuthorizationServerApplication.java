package com.rvillalba.samples.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Servidor de autorización OAuth2 / OpenID Connect.
 *
 * <p>Toda la configuración (clientes registrados, tipos de grant, scopes) está en
 * application.properties: Spring Boot autoconfigura los endpoints estándar
 * ({@code /oauth2/token}, {@code /oauth2/jwks}, {@code /.well-known/...}) y genera
 * una clave RSA para firmar los JWT.
 */
@SpringBootApplication
public class AuthorizationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }
}
