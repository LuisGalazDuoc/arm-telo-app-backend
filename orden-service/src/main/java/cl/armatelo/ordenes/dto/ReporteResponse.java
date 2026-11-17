package cl.armatelo.ordenes.dto;

import java.util.List;

/** Cifras de Admin › Reportes y Dashboard (mismo cálculo que calcularReporte() en AdminReportes.jsx). */
public record ReporteResponse(
        long ingresos,
        long cantidadPagadas,
        long cantidadRechazadas,
        long cantidadPendientes,
        long unidadesVendidas,
        List<ProductoVendido> masVendidos,
        List<VentaCategoria> ventasPorCategoria,
        long productosCriticos,
        long valorInventario,
        long totalProductos,
        long totalUsuarios,
        long totalOrdenes) {

    public record ProductoVendido(String nombre, long cantidad) {
    }

    public record VentaCategoria(String categoria, long total) {
    }
}
