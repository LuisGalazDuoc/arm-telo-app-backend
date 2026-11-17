package cl.armatelo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos editables del propio perfil (PerfilForm del frontend). El correo y el rol no se modifican.
 * "password" es opcional: si viene con texto, reemplaza la contraseña actual.
 */
public record PerfilRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 80) String nombre,
        @NotBlank(message = "Ingresa los apellidos.") @Size(max = 120) String apellidos,
        String telefono,
        String region,
        String comuna,
        String calle,
        String departamento,
        String password) {
}
