package cl.armatelo.ordenes.service;

import cl.armatelo.ordenes.client.CatalogoClient;
import cl.armatelo.ordenes.client.ProductoCatalogo;
import cl.armatelo.ordenes.dto.ClienteDto;
import cl.armatelo.ordenes.dto.DireccionDto;
import cl.armatelo.ordenes.dto.ItemOrdenRequest;
import cl.armatelo.ordenes.dto.OrdenRequest;
import cl.armatelo.ordenes.dto.OrdenResponse;
import cl.armatelo.ordenes.dto.PagoRequest;
import cl.armatelo.ordenes.dto.PagoResponse;
import cl.armatelo.ordenes.exception.ReglaNegocioException;
import cl.armatelo.ordenes.model.EstadoOrden;
import cl.armatelo.ordenes.model.Orden;
import cl.armatelo.ordenes.repository.OrdenRepository;
import cl.armatelo.ordenes.security.UsuarioToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Flujo de compra con catalogo-service simulado (Mockito). */
class OrdenServiceTest {

    private OrdenRepository ordenRepository;
    private CatalogoClient catalogo;
    private OrdenService servicio;
    private Orden ultimaGuardada;

    @BeforeEach
    void preparar() {
        ordenRepository = mock(OrdenRepository.class);
        catalogo = mock(CatalogoClient.class);
        servicio = new OrdenService(ordenRepository, catalogo);

        // Ryzen 5 5600: precio 129.990, oferta 114.990 (precioFinal), stock 3
        when(catalogo.obtenerProducto(10L))
                .thenReturn(new ProductoCatalogo(10L, "Ryzen 5 5600", 1L, "Procesadores", 129990, 3, 114990, false));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> {
            Orden o = inv.getArgument(0);
            o.setId(7L);
            ultimaGuardada = o;
            return o;
        });
    }

    private static OrdenRequest pedido(String entrega, int cantidad) {
        return new OrdenRequest(
                new ClienteDto("Ana", "Pérez", "ana@correo.cl"),
                new DireccionDto("Av. Siempre Viva 123", null, "Región Metropolitana", "Santiago", null),
                entrega,
                List.of(new ItemOrdenRequest(10L, cantidad)));
    }

    @Test
    @DisplayName("Calcula el subtotal con el precio de oferta y suma el despacho ($3.990)")
    void calculaTotales() {
        OrdenResponse r = servicio.crear(pedido("despacho", 2), null);

        assertEquals(229980, r.subtotal());
        assertEquals(3990, r.envio());
        assertEquals(233970, r.total());
        assertEquals("pendiente", r.estado());
        assertEquals("ORDER00007", r.codigo());
        assertTrue(r.numero().endsWith("07"));
        assertNull(r.usuarioId());
    }

    @Test
    @DisplayName("Si la compra trae token, la orden queda asociada al usuario")
    void asociaUsuario() {
        OrdenResponse r = servicio.crear(pedido("retiro", 1), new UsuarioToken(3L, "ana@correo.cl", "CLIENTE"));
        assertEquals(3L, r.usuarioId());
        assertEquals(0, r.envio());
    }

    @Test
    @DisplayName("Rechaza la orden si se pide más que el stock disponible")
    void validaStock() {
        var ex = assertThrows(ReglaNegocioException.class, () -> servicio.crear(pedido("retiro", 4), null));
        assertTrue(ex.getMessage().contains("Ryzen 5 5600"));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Despacho sin dirección completa es un error")
    void despachoSinDireccion() {
        var req = new OrdenRequest(new ClienteDto("Ana", "Pérez", "ana@correo.cl"), null, "despacho",
                List.of(new ItemOrdenRequest(10L, 1)));
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(req, null));
    }

    @Test
    @DisplayName("Pago aprobado: descuenta stock en catalogo-service y deja la orden pagada")
    void pagoAprobado() {
        servicio.crear(pedido("retiro", 2), null);
        when(ordenRepository.findById(7L)).thenReturn(Optional.of(ultimaGuardada));
        when(catalogo.descontarStock(anyList())).thenReturn(new CatalogoClient.ResultadoStock(true, List.of()));

        PagoResponse pago = servicio.pagar(7L, new PagoRequest("aprobado"));

        assertTrue(pago.ok());
        assertEquals("ORDER00007", pago.codigo());
        assertEquals(EstadoOrden.PAGADA, ultimaGuardada.getEstado());
    }

    @Test
    @DisplayName("Si catalogo-service informa que no hay stock, el pago se rechaza con el motivo")
    void pagoSinStock() {
        servicio.crear(pedido("retiro", 2), null);
        when(ordenRepository.findById(7L)).thenReturn(Optional.of(ultimaGuardada));
        when(catalogo.descontarStock(anyList())).thenReturn(new CatalogoClient.ResultadoStock(false, List.of("Ryzen 5 5600")));

        PagoResponse pago = servicio.pagar(7L, new PagoRequest("aprobado"));

        assertFalse(pago.ok());
        assertTrue(pago.motivo().contains("Ryzen 5 5600"));
        assertEquals(EstadoOrden.RECHAZADA, ultimaGuardada.getEstado());
    }

    @Test
    @DisplayName("Pago rechazado por el banco: no se llama a catalogo-service")
    void pagoRechazado() {
        servicio.crear(pedido("retiro", 2), null);
        when(ordenRepository.findById(7L)).thenReturn(Optional.of(ultimaGuardada));

        PagoResponse pago = servicio.pagar(7L, new PagoRequest("rechazado"));

        assertFalse(pago.ok());
        assertEquals(OrdenService.MOTIVO_RECHAZO_BANCO, pago.motivo());
        verify(catalogo, never()).descontarStock(anyList());
    }

    @Test
    @DisplayName("Una orden pagada no se puede volver a pagar")
    void noSePagaDosVeces() {
        servicio.crear(pedido("retiro", 1), null);
        ultimaGuardada.setEstado(EstadoOrden.PAGADA);
        when(ordenRepository.findById(7L)).thenReturn(Optional.of(ultimaGuardada));

        assertThrows(ReglaNegocioException.class, () -> servicio.pagar(7L, new PagoRequest("aprobado")));
    }

    @Test
    @DisplayName("Quién puede ver una boleta: admin siempre, cliente la suya, invitado con su correo")
    void accesoABoleta() {
        servicio.crear(pedido("retiro", 1), null);
        Orden o = ultimaGuardada;

        assertTrue(OrdenService.puedeVer(o, new UsuarioToken(1L, "admin@armatelo.cl", "ADMIN"), null));
        assertTrue(OrdenService.puedeVer(o, new UsuarioToken(3L, "ana@correo.cl", "CLIENTE"), null));
        assertFalse(OrdenService.puedeVer(o, new UsuarioToken(4L, "otro@correo.cl", "CLIENTE"), null));
        assertTrue(OrdenService.puedeVer(o, null, "ANA@correo.cl"));
        assertFalse(OrdenService.puedeVer(o, null, null));
    }
}
