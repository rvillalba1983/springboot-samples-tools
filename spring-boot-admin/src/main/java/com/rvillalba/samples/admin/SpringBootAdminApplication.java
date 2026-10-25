package com.rvillalba.samples.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

/**
 * Servidor de Spring Boot Admin. Para que el ejemplo muestre algo nada más arrancarlo,
 * la propia aplicación se registra también como cliente (ver application.properties).
 */
@SpringBootApplication
@EnableAdminServer
public class SpringBootAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }
}
