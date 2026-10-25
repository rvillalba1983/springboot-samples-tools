package com.rvillalba.samples.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private MessageSource messageSource;

    @Test
    void spanishWhenAcceptLanguageIsEs() {
        var result = mvc.get().uri("/greeting?name=Arya&surname=Stark").header("Accept-Language", "es-ES");

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.hello").isEqualTo("Hola Arya Stark");
        assertThat(result).bodyJson().extractingPath("$.farewell").isEqualTo("¡Adiós!");
    }

    @Test
    void frenchWhenAcceptLanguageIsFr() {
        var result = mvc.get().uri("/greeting").header("Accept-Language", "fr");

        assertThat(result).bodyJson().extractingPath("$.hello").isEqualTo("Bonjour Jon Snow");
    }

    @Test
    void defaultMessagesWhenLanguageIsNotSupported() {
        var result = mvc.get().uri("/greeting").header("Accept-Language", "de");

        assertThat(result).bodyJson().extractingPath("$.hello").isEqualTo("Hello Jon Snow");
    }

    @Test
    void messageSourceCanBeUsedDirectly() {
        assertThat(messageSource.getMessage("greeting.hello", new Object[] {"Jon", "Snow"}, Locale.of("es")))
                .isEqualTo("Hola Jon Snow");
    }
}
