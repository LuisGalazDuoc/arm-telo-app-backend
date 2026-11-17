package cl.armatelo.ordenes.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Producto tal como lo responde catalogo-service (GET /api/v1/productos). Solo los campos que se usan aquí. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoCatalogo(
        Long id,
        String nombre,
        Long categoriaId,
        String categoriaNombre,
        Integer precio,
        Integer stock,
        int precioFinal,
        boolean critico) {

    /** Stock sin definir (null) cuenta como 0, igual que en el frontend. */
    public int stockNumerico() {
        return stock == null || stock < 0 ? 0 : stock;
    }
}
