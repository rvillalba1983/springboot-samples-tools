# spring-mail

Envío de correo con `JavaMailSender`.

## Cómo funciona

Spring Boot crea el `JavaMailSender` en cuanto encuentra `spring.mail.host`:

```properties
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=usuario
spring.mail.password=contraseña
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
app.mail.from=no-reply@example.com
```

`EmailService` tiene dos métodos:
- `sendText`: texto plano a uno o varios destinatarios (`SimpleMailMessage`).
- `sendHtml`: HTML con alternativa en texto plano (`MimeMessageHelper`).

Si el envío no debe bloquear al llamante, basta con añadir `@Async` (ver [spring-async](../spring-async)).

## Tests

`EmailServiceTest` usa [GreenMail](https://greenmail-mail-test.github.io/greenmail/), un servidor SMTP en memoria que arranca la extensión de JUnit 5. Comprueba asunto, cuerpo, remitente, varios destinatarios y que el correo HTML incluye las dos partes.
