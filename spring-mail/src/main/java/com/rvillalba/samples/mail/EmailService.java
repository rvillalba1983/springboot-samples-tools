package com.rvillalba.samples.mail;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

/**
 * Spring Boot crea el {@link JavaMailSender} en cuanto existe {@code spring.mail.host}.
 * Si se quiere que el envío no bloquee al llamante, basta con añadir {@code @Async}
 * a los métodos (ver el módulo spring-async).
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    /** Correo de texto plano a uno o varios destinatarios. */
    public void sendText(String subject, String text, String... to) {
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /** Correo HTML con versión alternativa en texto plano. */
    public void sendHtml(String to, String subject, String plainText, String html) throws MessagingException {
        var mimeMessage = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(plainText, html);
        mailSender.send(mimeMessage);
    }
}
