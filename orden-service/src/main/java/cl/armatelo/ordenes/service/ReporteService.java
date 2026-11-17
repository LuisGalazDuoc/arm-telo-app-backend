package cl.armatelo.ordenes.service;

import cl.armatelo.ordenes.client.AuthClient;
import cl.armatelo.ordenes.client.CatalogoClient;
import cl.armatelo.ordenes.client.CategoriaCatalogo;
import cl.armatelo.ordenes.client.ProductoCatalogo;
import cl.armatelo.ordenes.dto.ReporteResponse;
import cl.armatelo.ordenes.dto.ReporteResponse.ProductoVendido;
import cl.armatelo.ordenes.dto.ReporteResponse.VentaCategoria;
import cl.armatelo.ordenes.model.EstadoOrden;
import cl.armatelo.ordenes.model.Orden;
import cl.armatelo.ordenes.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Reportes: combina las órdenes (propias) con productos y categorías (catalogo-service) y usuarios (auth-service). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final OrdenRepository ordenRepository;
    private final CatalogoClient catalogoClient;
    private final AuthClient authClient;

    public ReporteResponse resumen() {
        return calcular(ordenRepository.findAllByOrderByIdDesc(), catalogoClient.listarProductos(),
                catalogoClient.listarCategorias(), authClient.totalUsuarios());
    }

    /** Cálculo puro del reporte (se prueba sin base de datos ni otros servicios). */
    static ReporteResponse calcular(List<Orden> ordenes, List<ProductoCatalogo> productos,
                                    List<CategoriaCatalogo> categorias, long totalUsuarios) {
        Map<Long, String> nombreCategoria = categorias.stream()
                .collect(Collectors.toMap(CategoriaCatalogo::id, CategoriaCatalogo::nombre, (a, b) -> a));
        List<Orden> pagadas = ordenes.stream().filter(o -> o.getEstado() == EstadoOrden.PAGADA).toList();

        Map<String, Long> porProducto = new LinkedHashMap<>();
        Map<String, Long> porCategoria = new LinkedHashMap<>();
        pagadas.forEach(o -> o.getItems().forEach(i -> {
            porProducto.merge(i.getNombre(), (long) i.getCantidad(), Long::sum);
            String categoria = nombreCategoria.getOrDefault(i.getCategoriaId(), "Sin categoría");
            porCategoria.merge(categoria, (long) i.getSubtotal(), Long::sum);
        }));

        List<ProductoVendido> masVendidos = porProducto.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new ProductoVendido(e.getKey(), e.getValue()))
                .toList();
        List<VentaCategoria> ventasPorCategoria = porCategoria.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new VentaCategoria(e.getKey(), e.getValue()))
                .toList();

        return new ReporteResponse(
                pagadas.stream().mapToLong(Orden::getTotal).sum(),
                pagadas.size(),
                contar(ordenes, EstadoOrden.RECHAZADA),
                contar(ordenes, EstadoOrden.PENDIENTE),
                porProducto.values().stream().mapToLong(Long::longValue).sum(),
                masVendidos,
                ventasPorCategoria,
                productos.stream().filter(ProductoCatalogo::critico).count(),
                productos.stream().mapToLong(p -> (long) (p.precio() == null ? 0 : p.precio()) * p.stockNumerico()).sum(),
                productos.size(),
                totalUsuarios,
                ordenes.size());
    }

    /** Botón "Descargar órdenes (CSV)" de Admin › Reportes. */
    public String ordenesCsv() {
        StringBuilder csv = new StringBuilder("numero,fecha,cliente,correo,estado,total\n");
        Function<Object, String> celda = v -> "\"" + String.valueOf(v).replace("\"", "\"\"") + "\"";
        for (Orden o : ordenRepository.findAllByOrderByIdDesc()) {
            csv.append(String.join(",",
                    celda.apply(o.getNumero()),
                    celda.apply(o.getFecha()),
                    celda.apply(o.getCliente().getNombre() + " " + o.getCliente().getApellidos()),
                    celda.apply(o.getCliente().getCorreo()),
                    celda.apply(o.getEstado().valor()),
                    celda.apply(o.getTotal()))).append('\n');
        }
        return csv.toString();
    }

    private static long contar(List<Orden> ordenes, EstadoOrden estado) {
        return ordenes.stream().filter(o -> o.getEstado() == estado).count();
    }
}
