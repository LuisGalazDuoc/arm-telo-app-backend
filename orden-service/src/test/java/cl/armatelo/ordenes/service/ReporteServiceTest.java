package cl.armatelo.ordenes.service;

import cl.armatelo.ordenes.client.CategoriaCatalogo;
import cl.armatelo.ordenes.client.ProductoCatalogo;
import cl.armatelo.ordenes.dto.ReporteResponse;
import cl.armatelo.ordenes.model.DatosCliente;
import cl.armatelo.ordenes.model.DetalleOrden;
import cl.armatelo.ordenes.model.EstadoOrden;
import cl.armatelo.ordenes.model.Orden;
import cl.armatelo.ordenes.model.TipoEntrega;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReporteServiceTest {

    private static final ProductoCatalogo RYZEN = new ProductoCatalogo(1L, "Ryzen", 1L, "Procesadores", 100, 10, 100, false);

    @Test
    @DisplayName("Solo las órdenes pagadas suman ingresos y unidades vendidas")
    void calculaResumen() {
        Orden pagada = orden(EstadoOrden.PAGADA, 3);
        Orden rechazada = orden(EstadoOrden.RECHAZADA, 5);

        ReporteResponse r = ReporteService.calcular(List.of(pagada, rechazada), List.of(RYZEN),
                List.of(new CategoriaCatalogo(1L, "Procesadores")), 4);

        assertEquals(300, r.ingresos());
        assertEquals(1, r.cantidadPagadas());
        assertEquals(1, r.cantidadRechazadas());
        assertEquals(3, r.unidadesVendidas());
        assertEquals("Ryzen", r.masVendidos().getFirst().nombre());
        assertEquals("Procesadores", r.ventasPorCategoria().getFirst().categoria());
        assertEquals(1000, r.valorInventario());
        assertEquals(0, r.productosCriticos());
        assertEquals(4, r.totalUsuarios());
        assertEquals(2, r.totalOrdenes());
    }

    private static Orden orden(EstadoOrden estado, int cantidad) {
        Orden o = new Orden();
        o.setCliente(new DatosCliente("Ana", "Pérez", "ana@correo.cl"));
        o.setEntrega(TipoEntrega.RETIRO);
        o.agregarItem(DetalleOrden.desde(RYZEN, cantidad));
        o.setSubtotal(RYZEN.precioFinal() * cantidad);
        o.setEnvio(0);
        o.setTotal(o.getSubtotal());
        o.setEstado(estado);
        return o;
    }
}
