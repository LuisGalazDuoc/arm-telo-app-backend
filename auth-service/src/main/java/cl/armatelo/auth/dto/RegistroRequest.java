package cl.armatelo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Registro público desde la tienda (página Registro): siempre crea una cuenta con rol cliente. */
public record RegistroRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 80) String nombre,
        @NotBlank(message = "Ingresa los apellidos.") @Size(max = 120) String apellidos,
        @NotBlank(message = "Ingresa tu correo electrónico.") @Email(message = "Ingresa un correo válido.") String correo,
        @NotBlank(message = "Ingresa una contraseña.") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.") String password,
        String telefono,
        String region,
        String comuna,
        String calle,
        String departamento) {
}
