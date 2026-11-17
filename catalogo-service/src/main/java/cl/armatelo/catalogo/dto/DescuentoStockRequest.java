package cl.armatelo.catalogo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Lo envía orden-service cuando se aprueba el pago de una orden. */
public record DescuentoStockRequest(@NotEmpty List<@Valid Item> items) {

    public record Item(@NotNull Long productoId, @NotNull @Min(1) Integer cantidad) {
    }
}
