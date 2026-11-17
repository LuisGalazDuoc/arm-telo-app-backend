package cl.armatelo.contacto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Mismos campos y mensajes de validación que el formulario Contacto.jsx. */
public record MensajeRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 80) String nombre,
        @NotBlank(message = "Ingresa tu correo electrónico.") @Email(message = "Ingresa un correo válido.") String correo,
        @NotBlank(message = "Ingresa el asunto.") @Size(max = 150) String asunto,
        @NotBlank(message = "Ingresa el mensaje.") @Size(max = 2000) String mensaje) {
}
