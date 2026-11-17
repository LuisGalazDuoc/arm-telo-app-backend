package cl.armatelo.ordenes.dto;

/** Igual que la respuesta de pagarOrden() del frontend: { ok, codigo } o { ok: false, motivo }. */
public record PagoResponse(boolean ok, String codigo, String motivo, OrdenResponse orden) {
}
