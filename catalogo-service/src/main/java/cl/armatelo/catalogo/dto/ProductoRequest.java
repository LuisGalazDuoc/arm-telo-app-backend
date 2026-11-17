package cl.armatelo.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Formulario Admin › Productos. precioOferta y stock son opcionales:
 * el frontend envía null cuando el campo queda vacío (sin oferta / stock por definir).
 */
public record ProductoRequest(
        @NotBlank(message = "Ingresa el nombre.") @Size(max = 150) String nombre,
        @Size(max = 1000) String descripcion,
        @Size(max = 80) String marca,
        @NotNull(message = "Selecciona una categoría.") Long categoriaId,
        @NotNull(message = "El precio es obligatorio.")
        @PositiveOrZero(message = "El precio debe ser un número igual o mayor a 0.") Integer precio,
        @PositiveOrZero(message = "El precio de oferta debe ser un número igual o mayor a 0.") Integer precioOferta,
        @PositiveOrZero(message = "El stock debe ser un número igual o mayor a 0.") Integer stock,
        @NotNull(message = "El stock crítico es obligatorio.")
        @PositiveOrZero(message = "El stock crítico debe ser un número igual o mayor a 0.") Integer stockCritico,
        @Size(max = 500) String imagen) {
}
