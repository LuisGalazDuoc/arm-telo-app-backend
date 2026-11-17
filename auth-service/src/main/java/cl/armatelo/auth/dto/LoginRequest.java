package cl.armatelo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Ingresa tu correo electrónico.") @Email(message = "Ingresa un correo válido.") String correo,
        @NotBlank(message = "Ingresa tu contraseña.") String password) {
}
