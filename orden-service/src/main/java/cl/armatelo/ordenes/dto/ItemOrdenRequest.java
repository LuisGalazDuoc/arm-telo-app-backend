package cl.armatelo.ordenes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemOrdenRequest(
        @NotNull(message = "Falta el producto.") Long productoId,
        @NotNull(message = "Falta la cantidad.") @Min(value = 1, message = "La cantidad mínima es 1.") Integer cantidad) {
}
