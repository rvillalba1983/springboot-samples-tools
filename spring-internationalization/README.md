# spring-internationalization

Textos traducidos con `MessageSource`.

## Cómo funciona

- Spring Boot crea el `MessageSource` automáticamente a partir de los ficheros `messages*.properties` (`spring.messages.basename`), leídos en UTF-8.
- El idioma de cada petición sale de la cabecera `Accept-Language`, y en el controlador basta con recibir un parámetro `Locale`.
- Con `spring.messages.fallback-to-system-locale=false`, si el idioma pedido no existe se usa `messages.properties`, no el idioma del servidor.

Ficheros incluidos: `messages.properties` (inglés, por defecto), `messages_es.properties` y `messages_fr.properties`.

Mensajes con parámetros:

```properties
greeting.hello=Hola {0} {1}
```

```java
messageSource.getMessage("greeting.hello", new Object[] {"Jon", "Snow"}, locale);
```

## Probar

```bash
mvn spring-boot:run
curl -H "Accept-Language: es" "localhost:8080/greeting?name=Arya&surname=Stark"
# {"hello":"Hola Arya Stark","farewell":"¡Adiós!"}
```

## Tests

`GreetingControllerTest` comprueba la respuesta en español, en francés y con un idioma no soportado (que cae al inglés), y el uso directo del `MessageSource`.
