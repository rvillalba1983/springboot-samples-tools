package com.rvillalba.samples.authserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationServerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void issuesAccessTokenWithClientCredentials() {
        var result = mvc.post().uri("/oauth2/token")
                .with(httpBasic("samples-client", "samples-secret"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("scope", "samples.read");

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.token_type").isEqualTo("Bearer");
        assertThat(result).bodyJson().extractingPath("$.scope").isEqualTo("samples.read");
        assertThat(result).bodyJson().extractingPath("$.access_token").asString().isNotBlank();
    }

    @Test
    void rejectsWrongClientSecret() {
        var result = mvc.post().uri("/oauth2/token")
                .with(httpBasic("samples-client", "wrong"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials");

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void rejectsScopeNotRegisteredForTheClient() {
        var result = mvc.post().uri("/oauth2/token")
                .with(httpBasic("samples-client", "samples-secret"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "client_credentials")
                .param("scope", "admin");

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.error").isEqualTo("invalid_scope");
    }

    @Test
    void publishesServerMetadataAndPublicKeys() {
        assertThat(mvc.get().uri("/.well-known/oauth-authorization-server"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.token_endpoint").asString().endsWith("/oauth2/token");

        assertThat(mvc.get().uri("/oauth2/jwks"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.keys[0].kty").isEqualTo("RSA");
    }
}
