package cl.armatelo.auth.dto;

/** Respuesta de login/registro: el frontend guarda el token y lo envía en "Authorization: Bearer ...". */
public record AuthResponse(String token, String tipo, long expiraEnMinutos, UsuarioResponse usuario) {

    public static AuthResponse bearer(String token, long expiraEnMinutos, UsuarioResponse usuario) {
        return new AuthResponse(token, "Bearer", expiraEnMinutos, usuario);
    }
}
