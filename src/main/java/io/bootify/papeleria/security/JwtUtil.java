package io.bootify.papeleria.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final String jwtSecret;
    private final long jwtExpirationMs;
    private SecretKey signingKey;

    public JwtUtil(@Value("${jwt.secret:}") String jwtSecret,
                   @Value("${jwt.expiration-ms:0}") long jwtExpirationMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @PostConstruct
    private void init() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("jwt.secret no está definido. Añade 'jwt.secret' en application.properties o variable de entorno.");
        }
        if (jwtExpirationMs <= 0) {
            throw new IllegalStateException("jwt.expiration-ms no válido. Define 'jwt.expiration-ms' (milisegundos) en application.properties.");
        }

        // Si la secret está en base64 (si es tu caso) usa Decoders.BASE64.decode(jwtSecret)
        // Aquí asumimos texto plano y convertimos a bytes; mejor usa una clave suficientemente larga.
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        // En caso de que quieras usar base64, descomenta:
        // byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}