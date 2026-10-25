package com.rvillalba.samples.resourceserver;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/public/ping")
    public String ping() {
        return "pong";
    }

    /** Requiere el scope samples.read. Devuelve los datos del token recibido. */
    @GetMapping("/api/me")
    public TokenInfo me(@AuthenticationPrincipal Jwt jwt) {
        return new TokenInfo(jwt.getSubject(), jwt.getClaimAsStringList("scope"), String.valueOf(jwt.getIssuer()));
    }

    /** Requiere el scope samples.write. */
    @PostMapping("/api/notes")
    public Note createNote(@RequestBody Note note, @AuthenticationPrincipal Jwt jwt) {
        return new Note(note.text(), jwt.getSubject());
    }

    public record TokenInfo(String subject, List<String> scopes, String issuer) {
    }

    public record Note(String text, String author) {
    }
}
