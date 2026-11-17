package cl.armatelo.ordenes.dto;

import cl.armatelo.ordenes.model.DetalleOrden;

public record DetalleOrdenResponse(Long productoId, Long categoriaId, String nombre, Integer precio, Integer cantidad, Integer subtotal) {

    public static DetalleOrdenResponse desde(DetalleOrden d) {
        return new DetalleOrdenResponse(d.getProductoId(), d.getCategoriaId(), d.getNombre(), d.getPrecio(), d.getCantidad(), d.getSubtotal());
    }
}
