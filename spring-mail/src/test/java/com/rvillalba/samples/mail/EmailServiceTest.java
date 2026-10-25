package com.rvillalba.samples.mail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMultipart;

/**
 * GreenMail levanta un servidor SMTP en memoria (puerto 3025) para comprobar los correos
 * enviados sin depender de un servidor real.
 */
@SpringBootTest(properties = {"spring.mail.host=localhost", "spring.mail.port=3025"})
class EmailServiceTest {

    @RegisterExtension
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withPerMethodLifecycle(true);

    @Autowired
    private EmailService emailService;

    @Test
    void sendsPlainTextEmail() throws Exception {
        emailService.sendText("Prueba", "Correo de prueba", "alguien@example.com");

        assertThat(greenMail.waitForIncomingEmail(5_000, 1)).isTrue();
        var message = greenMail.getReceivedMessages()[0];
        assertThat(message.getSubject()).isEqualTo("Prueba");
        assertThat(GreenMailUtil.getBody(message)).isEqualTo("Correo de prueba");
        assertThat(message.getFrom()[0]).hasToString("no-reply@example.com");
    }

    @Test
    void sendsToSeveralRecipients() throws Exception {
        emailService.sendText("Aviso", "Para todos", "uno@example.com", "dos@example.com");

        assertThat(greenMail.waitForIncomingEmail(5_000, 2)).isTrue();
        assertThat(greenMail.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .containsExactlyInAnyOrder("uno@example.com", "dos@example.com");
    }

    @Test
    void sendsHtmlEmailWithPlainTextAlternative() throws Exception {
        emailService.sendHtml("alguien@example.com", "Bienvenida", "Hola", "<h1>Hola</h1>");

        assertThat(greenMail.waitForIncomingEmail(5_000, 1)).isTrue();
        var message = greenMail.getReceivedMessages()[0];
        assertThat(message.getContent()).isInstanceOf(MimeMultipart.class);
        assertThat(GreenMailUtil.getWholeMessage(message))
                .contains("text/plain")
                .contains("text/html")
                .contains("<h1>Hola</h1>");
    }
}
