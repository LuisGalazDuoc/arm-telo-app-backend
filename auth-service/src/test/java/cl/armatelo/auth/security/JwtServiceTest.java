package cl.armatelo.auth.security;

import cl.armatelo.auth.model.Rol;
import cl.armatelo.auth.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El token lo emite jjwt (auth-service) y lo valida el Resource Server de Spring (todos los servicios).
 * Esta prueba asegura que ambos lados son compatibles.
 */
class JwtServiceTest {

    private static final String SECRETO = "clave-de-pruebas-con-mas-de-32-caracteres-para-hs256";

    private static Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(5L);
        u.setCorreo("cliente@armatelo.cl");
        u.setRol(Rol.CLIENTE);
        return u;
    }

    @Test
    @DisplayName("El token emitido lo acepta el decodificador de los microservicios y conserva correo, id y rol")
    void tokenCompatibleConResourceServer() {
        String token = new JwtService(SECRETO, 60).generarToken(usuario());

        Jwt jwt = SecurityConfig.decodificador(SECRETO).decode(token);

        assertEquals("cliente@armatelo.cl", jwt.getSubject());
        assertEquals("CLIENTE", jwt.getClaimAsString("rol"));
        UsuarioToken u = UsuarioToken.desde(jwt);
        assertEquals(5L, u.id());
    }

    @Test
    @DisplayName("El claim rol se convierte en la autoridad ROLE_CLIENTE")
    void rolComoAutoridad() {
        String token = new JwtService(SECRETO, 60).generarToken(usuario());
        Jwt jwt = SecurityConfig.decodificador(SECRETO).decode(token);

        var autoridades = SecurityConfig.convertidorDeRoles().convert(jwt).getAuthorities();

        assertTrue(autoridades.stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_CLIENTE"::equals));
    }

    @Test
    @DisplayName("Se rechaza un token firmado con otra clave")
    void rechazaOtraClave() {
        String token = new JwtService("otra-clave-distinta-tambien-de-mas-de-32-caracteres", 60).generarToken(usuario());
        assertThrows(JwtException.class, () -> SecurityConfig.decodificador(SECRETO).decode(token));
    }

    @Test
    @DisplayName("Se rechaza un token expirado")
    void rechazaExpirado() {
        String token = new JwtService(SECRETO, -5).generarToken(usuario());
        assertThrows(JwtException.class, () -> SecurityConfig.decodificador(SECRETO).decode(token));
    }

    @Test
    @DisplayName("Exige una clave de al menos 32 caracteres")
    void exigeClaveLarga() {
        assertThrows(IllegalStateException.class, () -> new JwtService("corta", 60));
    }
}
