package cl.armatelo.ordenes.dto;

import jakarta.validation.constraints.Pattern;

/**
 * Pago simulado (RESULTADOS_PAGO en el frontend): no hay pasarela real,
 * el resultado lo elige la persona en el checkout. Si no se envía, se asume "aprobado".
 */
public record PagoRequest(
        @Pattern(regexp = "(?i)aprobado|rechazado", message = "El resultado debe ser aprobado o rechazado.") String resultado) {

    public boolean aprobado() {
        return resultado == null || resultado.equalsIgnoreCase("aprobado");
    }
}
