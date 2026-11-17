package cl.armatelo.catalogo.dto;

import cl.armatelo.catalogo.model.Producto;

/** Producto con los campos del frontend y las reglas ya calculadas (oferta, precio final y stock crítico). */
public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        String marca,
        Long categoriaId,
        String categoriaNombre,
        Integer precio,
        Integer precioOferta,
        Integer stock,
        Integer stockCritico,
        String imagen,
        boolean enOferta,
        int precioFinal,
        boolean critico) {

    public static ProductoResponse desde(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getDescripcion(), p.getMarca(),
                p.getCategoria().getId(), p.getCategoria().getNombre(),
                p.getPrecio(), p.getPrecioOferta(), p.getStock(), p.getStockCritico(), p.getImagen(),
                p.enOferta(), p.precioFinal(), p.esCritico());
    }
}
