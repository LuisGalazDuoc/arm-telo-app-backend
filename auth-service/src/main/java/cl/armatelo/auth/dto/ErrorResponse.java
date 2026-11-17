package cl.armatelo.auth.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato único de error (igual en todos los microservicios). "campos" trae los errores de validación. */
public record ErrorResponse(int status, String error, String mensaje, String ruta, Map<String, String> campos, LocalDateTime fecha) {

    public static ErrorResponse de(int status, String error, String mensaje, String ruta) {
        return new ErrorResponse(status, error, mensaje, ruta, null, LocalDateTime.now());
    }
}
