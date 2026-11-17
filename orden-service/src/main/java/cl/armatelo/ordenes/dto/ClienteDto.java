package cl.armatelo.ordenes.dto;

import cl.armatelo.ordenes.model.DatosCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDto(
        @NotBlank(message = "Ingresa el nombre.") String nombre,
        @NotBlank(message = "Ingresa los apellidos.") String apellidos,
        @NotBlank(message = "Ingresa tu correo electrónico.") @Email(message = "Ingresa un correo válido.") String correo) {

    public static ClienteDto desde(DatosCliente c) {
        return c == null ? null : new ClienteDto(c.getNombre(), c.getApellidos(), c.getCorreo());
    }
}
