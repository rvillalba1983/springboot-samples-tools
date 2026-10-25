package com.rvillalba.samples.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.boot.admin.client.enabled=false")
class SpringBootAdminApplicationTest {

    @Value("${local.server.port}")
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create("http://localhost:" + port);
    }

    @Test
    void adminServerExposesTheInstancesApi() {
        var response = restClient.get().uri("/instances")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).startsWith("[");
    }

    @Test
    void actuatorHealthIsUp() {
        var health = restClient.get().uri("/actuator/health").retrieve().body(String.class);

        assertThat(health).contains("\"status\":\"UP\"");
    }
}
