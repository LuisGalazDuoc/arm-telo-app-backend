package cl.armatelo.auth.dto;

import cl.armatelo.auth.model.Usuario;

import java.time.LocalDateTime;

/** Mismos campos que usa el frontend para un usuario (nunca incluye la contraseña). */
public record UsuarioResponse(
        Long id,
        String nombre,
        String apellidos,
        String correo,
        String rol,
        String telefono,
        String region,
        String comuna,
        String calle,
        String departamento,
        LocalDateTime fechaRegistro) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getApellidos(), u.getCorreo(), u.getRol().valor(),
                u.getTelefono(), u.getRegion(), u.getComuna(), u.getCalle(), u.getDepartamento(), u.getFechaRegistro());
    }
}
