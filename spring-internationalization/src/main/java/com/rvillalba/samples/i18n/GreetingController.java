package com.rvillalba.samples.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot crea el {@link MessageSource} automáticamente a partir de {@code messages*.properties}
 * y resuelve el {@link Locale} de cada petición a partir de la cabecera {@code Accept-Language}.
 * Basta con inyectar el Locale como parámetro del método.
 */
@RestController
public class GreetingController {

    private final MessageSource messageSource;

    public GreetingController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(defaultValue = "Jon") String name,
                             @RequestParam(defaultValue = "Snow") String surname,
                             Locale locale) {
        return new Greeting(
                messageSource.getMessage("greeting.hello", new Object[] {name, surname}, locale),
                messageSource.getMessage("greeting.farewell", null, locale));
    }

    public record Greeting(String hello, String farewell) {
    }
}
