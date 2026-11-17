package cl.armatelo.catalogo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Protege los endpoints /internal/** que usan otros microservicios.
 * Esas rutas no se publican en el API Gateway y además exigen el encabezado X-Clave-Interna.
 */
@Component
public class ClaveInterna {

    public static final String ENCABEZADO = "X-Clave-Interna";

    private final byte[] clave;

    public ClaveInterna(@Value("${armatelo.interno.clave}") String clave) {
        this.clave = clave.getBytes(StandardCharsets.UTF_8);
    }

    public void validar(String recibida) {
        if (recibida == null || !MessageDigest.isEqual(clave, recibida.getBytes(StandardCharsets.UTF_8))) {
            throw new AccessDeniedException("Clave interna inválida.");
        }
    }
}
