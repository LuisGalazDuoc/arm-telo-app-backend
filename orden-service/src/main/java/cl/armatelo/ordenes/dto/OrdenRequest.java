package cl.armatelo.ordenes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Datos del Checkout. Solo se reciben ids y cantidades: precios, envío y total
 * se calculan en el servidor con los datos de catalogo-service.
 */
public record OrdenRequest(
        @NotNull(message = "Faltan los datos del cliente.") @Valid ClienteDto cliente,
        @Valid DireccionDto direccion,
        @Pattern(regexp = "(?i)despacho|retiro", message = "La entrega debe ser despacho o retiro.") String entrega,
        @NotEmpty(message = "El carrito está vacío.") List<@Valid ItemOrdenRequest> items) {
}
