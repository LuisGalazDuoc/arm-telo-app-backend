package cl.armatelo.auth.security;

import cl.armatelo.auth.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Emite los tokens JWT (solo auth-service los crea; todos los microservicios los validan).
 * Firma HMAC-SHA256 con la clave compartida. Contenido: sub = correo, id, rol (ADMIN | CLIENTE), iat y exp.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMinutos;

    public JwtService(@Value("${armatelo.jwt.secret}") String secreto,
                      @Value("${armatelo.jwt.expiracion-minutos}") long expiracionMinutos) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("armatelo.jwt.secret debe tener al menos 32 caracteres.");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.expiracionMinutos = expiracionMinutos;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES)))
                // Se fija HS256 explícitamente: es el algoritmo que esperan los demás microservicios.
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    public long getExpiracionMinutos() {
        return expiracionMinutos;
    }
}
