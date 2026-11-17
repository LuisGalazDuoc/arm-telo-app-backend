package cl.armatelo.ordenes.security;

import org.springframework.security.oauth2.jwt.Jwt;

/** Datos del usuario que vienen dentro del JWT validado. */
public record UsuarioToken(Long id, String correo, String rol) {

    /** Devuelve null cuando la petición no trae token. */
    public static UsuarioToken desde(Jwt jwt) {
        if (jwt == null) return null;
        Object id = jwt.getClaim("id");
        return new UsuarioToken(id instanceof Number n ? n.longValue() : null, jwt.getSubject(), jwt.getClaimAsString("rol"));
    }

    public boolean esAdmin() {
        return "ADMIN".equals(rol);
    }
}
