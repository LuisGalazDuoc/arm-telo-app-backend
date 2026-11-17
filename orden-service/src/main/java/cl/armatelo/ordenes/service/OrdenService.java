package cl.armatelo.ordenes.service;

import cl.armatelo.ordenes.client.CatalogoClient;
import cl.armatelo.ordenes.client.ProductoCatalogo;
import cl.armatelo.ordenes.dto.DireccionDto;
import cl.armatelo.ordenes.dto.ItemOrdenRequest;
import cl.armatelo.ordenes.dto.OrdenRequest;
import cl.armatelo.ordenes.dto.OrdenResponse;
import cl.armatelo.ordenes.dto.PagoRequest;
import cl.armatelo.ordenes.dto.PagoResponse;
import cl.armatelo.ordenes.exception.RecursoNoEncontradoException;
import cl.armatelo.ordenes.exception.ReglaNegocioException;
import cl.armatelo.ordenes.model.DatosCliente;
import cl.armatelo.ordenes.model.DetalleOrden;
import cl.armatelo.ordenes.model.Direccion;
import cl.armatelo.ordenes.model.EstadoOrden;
import cl.armatelo.ordenes.model.Orden;
import cl.armatelo.ordenes.model.TipoEntrega;
import cl.armatelo.ordenes.repository.OrdenRepository;
import cl.armatelo.ordenes.security.UsuarioToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Flujo de compra (src/services/compras.js del frontend):
 * crear la orden (PENDIENTE) → pagar (simulado) → PAGADA (descuenta stock en catalogo-service) o RECHAZADA.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdenService {

    static final String MOTIVO_RECHAZO_BANCO = "El banco rechazó la transacción.";
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OrdenRepository ordenRepository;
    private final CatalogoClient catalogoClient;

    // ---------- Crear (Checkout) ----------

    /**
     * Crea una orden PENDIENTE. Precios y stock se consultan a catalogo-service.
     *
     * @param yo usuario del token, o null si compra como invitado
     */
    @Transactional
    public OrdenResponse crear(OrdenRequest req, UsuarioToken yo) {
        TipoEntrega entrega = TipoEntrega.desde(req.entrega());
        Direccion direccion = validarDireccion(entrega, req.direccion());

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemOrdenRequest item : req.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Orden orden = new Orden();
        List<String> sinStock = new ArrayList<>();
        cantidades.forEach((productoId, cantidad) -> {
            ProductoCatalogo producto = catalogoClient.obtenerProducto(productoId);
            if (cantidad > producto.stockNumerico()) sinStock.add(producto.nombre());
            orden.agregarItem(DetalleOrden.desde(producto, cantidad));
        });
        if (!sinStock.isEmpty()) {
            throw ReglaNegocioException.conflicto("No hay stock suficiente de: " + String.join(", ", sinStock)
                    + ". Ajusta las cantidades en tu carrito.");
        }

        int subtotal = orden.getItems().stream().mapToInt(DetalleOrden::getSubtotal).sum();
        int envio = entrega.getCosto();
        orden.setCliente(new DatosCliente(req.cliente().nombre().trim(), req.cliente().apellidos().trim(),
                req.cliente().correo().trim().toLowerCase()));
        orden.setDireccion(direccion);
        orden.setEntrega(entrega);
        orden.setSubtotal(subtotal);
        orden.setEnvio(envio);
        orden.setTotal(subtotal + envio);
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setUsuarioId(yo != null ? yo.id() : null);

        Orden guardada = ordenRepository.save(orden);
        guardada.setNumero(LocalDateTime.now().format(FORMATO_DIA) + "%02d".formatted(guardada.getId()));
        return OrdenResponse.desde(guardada);
    }

    // ---------- Pagar (simulación) ----------

    @Transactional
    public PagoResponse pagar(Long id, PagoRequest req) {
        Orden orden = buscar(id);
        if (orden.getEstado() == EstadoOrden.PAGADA) {
            throw ReglaNegocioException.conflicto("La orden ya fue pagada.");
        }
        if (req != null && !req.aprobado()) {
            return rechazar(orden, MOTIVO_RECHAZO_BANCO);
        }

        // catalogo-service vuelve a validar el stock y lo descuenta en una sola operación.
        CatalogoClient.ResultadoStock stock = catalogoClient.descontarStock(orden.getItems());
        if (stock == null || !stock.ok()) {
            String faltantes = stock == null ? "" : String.join(", ", stock.sinStock());
            return rechazar(orden, "No hay stock suficiente de: " + faltantes + ".");
        }

        orden.setEstado(EstadoOrden.PAGADA);
        orden.setMotivo(null);
        orden.setFechaPago(LocalDateTime.now());
        return new PagoResponse(true, orden.codigo(), null, OrdenResponse.desde(orden));
    }

    private PagoResponse rechazar(Orden orden, String motivo) {
        orden.setEstado(EstadoOrden.RECHAZADA);
        orden.setMotivo(motivo);
        return new PagoResponse(false, null, motivo, OrdenResponse.desde(orden));
    }

    // ---------- Consultas ----------

    /**
     * Admin › Órdenes (filtro por estado) y Admin › Usuarios › Historial (filtro por usuarioId y/o correo).
     */
    public List<OrdenResponse> listar(String estado, Long usuarioId, String correo) {
        List<Orden> ordenes;
        if (usuarioId != null || !Textos.vacio(correo)) {
            ordenes = ordenRepository.buscarHistorial(usuarioId, Textos.limpiar(correo));
        } else if (!Textos.vacio(estado)) {
            ordenes = ordenRepository.findByEstadoOrderByIdDesc(EstadoOrden.desde(estado));
        } else {
            ordenes = ordenRepository.findAllByOrderByIdDesc();
        }
        return ordenes.stream().map(OrdenResponse::desde).toList();
    }

    /** Perfil › Mis compras. */
    public List<OrdenResponse> mias(UsuarioToken yo) {
        return ordenRepository.buscarHistorial(yo.id(), yo.correo()).stream().map(OrdenResponse::desde).toList();
    }

    /**
     * Boleta (CompraExitosa, CompraError, AdminBoleta). Pueden verla:
     * ADMIN siempre; un cliente si es suya; un invitado si indica el correo usado en la compra.
     */
    public OrdenResponse obtener(Long id, UsuarioToken yo, String correo) {
        Orden orden = buscar(id);
        if (!puedeVer(orden, yo, correo)) {
            throw new AccessDeniedException("No tienes acceso a esta orden.");
        }
        return OrdenResponse.desde(orden);
    }

    static boolean puedeVer(Orden orden, UsuarioToken yo, String correo) {
        String correoOrden = orden.getCliente().getCorreo();
        if (yo != null) {
            if (yo.esAdmin()) return true;
            boolean esSuya = yo.id() != null && yo.id().equals(orden.getUsuarioId());
            if (esSuya || (yo.correo() != null && yo.correo().equalsIgnoreCase(correoOrden))) return true;
        }
        return correo != null && correo.trim().equalsIgnoreCase(correoOrden);
    }

    // ---------- Apoyo ----------

    private static Direccion validarDireccion(TipoEntrega entrega, DireccionDto d) {
        if (entrega == TipoEntrega.RETIRO) return null;
        if (d == null || Textos.vacio(d.calle()) || Textos.vacio(d.region()) || Textos.vacio(d.comuna())) {
            throw new ReglaNegocioException("Para despacho a domicilio ingresa calle, región y comuna.");
        }
        return new Direccion(d.calle().trim(), Textos.limpiar(d.departamento()), d.region().trim(), d.comuna().trim(),
                Textos.limpiar(d.indicaciones()));
    }

    private Orden buscar(Long id) {
        return ordenRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("la orden", id));
    }
}
