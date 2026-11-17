package cl.armatelo.ordenes.dto;

import cl.armatelo.ordenes.model.Orden;

import java.time.LocalDateTime;
import java.util.List;

/** Misma forma que las órdenes del frontend (ResumenOrden, AdminBoleta, Perfil). */
public record OrdenResponse(
        Long id,
        String numero,
        String codigo,
        Long usuarioId,
        ClienteDto cliente,
        DireccionDto direccion,
        String entrega,
        List<DetalleOrdenResponse> items,
        Integer subtotal,
        Integer envio,
        Integer total,
        String estado,
        String motivo,
        LocalDateTime fecha,
        LocalDateTime fechaPago) {

    public static OrdenResponse desde(Orden o) {
        return new OrdenResponse(o.getId(), o.getNumero(), o.codigo(), o.getUsuarioId(),
                ClienteDto.desde(o.getCliente()), DireccionDto.desde(o.getDireccion()), o.getEntrega().valor(),
                o.getItems().stream().map(DetalleOrdenResponse::desde).toList(),
                o.getSubtotal(), o.getEnvio(), o.getTotal(), o.getEstado().valor(), o.getMotivo(),
                o.getFecha(), o.getFechaPago());
    }
}
