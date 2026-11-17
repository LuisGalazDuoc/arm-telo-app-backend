package cl.armatelo.ordenes.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Categoría tal como la responde catalogo-service (GET /api/v1/categorias). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoriaCatalogo(Long id, String nombre) {
}
