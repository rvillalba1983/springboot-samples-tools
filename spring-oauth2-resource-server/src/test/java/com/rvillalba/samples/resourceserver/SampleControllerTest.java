package com.rvillalba.samples.resourceserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * {@code jwt()} de spring-security-test simula un token ya validado, así que no hace falta
 * levantar el servidor de autorización. El JwtDecoder se sustituye por un mock para que
 * el contexto no intente contactar con el issuer.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SampleControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void publicEndpointDoesNotNeedToken() {
        assertThat(mvc.get().uri("/public/ping")).hasStatus(HttpStatus.OK).hasBodyTextEqualTo("pong");
    }

    @Test
    void withoutTokenReturns401() {
        assertThat(mvc.get().uri("/api/me")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void tokenWithoutRequiredScopeReturns403() {
        var result = mvc.get().uri("/api/me")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_other")));

        assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    void tokenWithReadScopeCanReadButNotWrite() {
        var token = jwt()
                .jwt(builder -> builder.subject("samples-client").claim("scope", "samples.read"))
                .authorities(new SimpleGrantedAuthority("SCOPE_samples.read"));

        var read = mvc.get().uri("/api/me").with(token);
        assertThat(read).hasStatus(HttpStatus.OK);
        assertThat(read).bodyJson().extractingPath("$.subject").isEqualTo("samples-client");

        var write = mvc.post().uri("/api/notes").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"hola\"}");
        assertThat(write).hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    void tokenWithWriteScopeCanCreateNotes() {
        var result = mvc.post().uri("/api/notes")
                .with(jwt().jwt(builder -> builder.subject("samples-client"))
                        .authorities(new SimpleGrantedAuthority("SCOPE_samples.write")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"hola\"}");

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.author").isEqualTo("samples-client");
    }
}
