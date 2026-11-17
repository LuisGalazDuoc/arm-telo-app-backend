package cl.armatelo.ordenes.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/** Comunicación REST con auth-service (endpoint interno para los reportes). */
@Component
public class AuthClient {

    private final RestClient rest;
    private final String claveInterna;

    public AuthClient(RestClient.Builder builder,
                      @Value("${armatelo.servicios.auth-url}") String url,
                      @Value("${armatelo.interno.clave}") String claveInterna) {
        this.rest = builder.baseUrl(url).build();
        this.claveInterna = claveInterna;
    }

    public long totalUsuarios() {
        Map<String, Long> respuesta = rest.get().uri("/internal/usuarios/total")
                .header(CatalogoClient.ENCABEZADO_CLAVE, claveInterna)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return respuesta == null ? 0 : respuesta.getOrDefault("total", 0L);
    }
}
