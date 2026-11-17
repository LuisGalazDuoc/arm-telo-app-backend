package cl.armatelo.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 80) String nombre,
        @Size(max = 300) String descripcion,
        @Size(max = 50) String icono) {
}
