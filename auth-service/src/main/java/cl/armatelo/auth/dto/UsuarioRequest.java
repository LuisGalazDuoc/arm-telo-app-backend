package cl.armatelo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Alta/edición de usuarios desde Admin › Usuarios.
 * La contraseña es obligatoria al crear; al editar, si viene vacía se mantiene la actual.
 */
public record UsuarioRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 80) String nombre,
        @NotBlank(message = "Ingresa los apellidos.") @Size(max = 120) String apellidos,
        @NotBlank(message = "Ingresa el correo electrónico.") @Email(message = "Ingresa un correo válido.") String correo,
        String password,
        @Pattern(regexp = "(?i)admin|cliente", message = "El rol debe ser admin o cliente.") String rol,
        String telefono,
        String region,
        String comuna,
        String calle,
        String departamento) {
}
