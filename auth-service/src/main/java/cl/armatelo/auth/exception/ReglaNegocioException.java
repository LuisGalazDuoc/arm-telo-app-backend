package cl.armatelo.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Una regla de negocio no se cumple (correo repetido, credenciales incorrectas, etc.). */
@Getter
public class ReglaNegocioException extends RuntimeException {

    private final HttpStatus status;

    public ReglaNegocioException(String mensaje) {
        this(HttpStatus.BAD_REQUEST, mensaje);
    }

    public ReglaNegocioException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public static ReglaNegocioException conflicto(String mensaje) {
        return new ReglaNegocioException(HttpStatus.CONFLICT, mensaje);
    }
}
