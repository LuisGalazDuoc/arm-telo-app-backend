package cl.armatelo.ordenes.client;

import cl.armatelo.ordenes.exception.RecursoNoEncontradoException;
import cl.armatelo.ordenes.model.DetalleOrden;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Comunicación REST con catalogo-service.
 * - Lectura de productos y categorías: endpoints públicos /api/v1/...
 * - Descuento de stock: endpoint interno /internal/... con el encabezado X-Clave-Interna.
 */
@Component
public class CatalogoClient {

    static final String ENCABEZADO_CLAVE = "X-Clave-Interna";

    private final RestClient rest;
    private final String claveInterna;

    public CatalogoClient(RestClient.Builder builder,
                          @Value("${armatelo.servicios.catalogo-url}") String url,
                          @Value("${armatelo.interno.clave}") String claveInterna) {
        this.rest = builder.baseUrl(url).build();
        this.claveInterna = claveInterna;
    }

    public ProductoCatalogo obtenerProducto(Long id) {
        try {
            return rest.get().uri("/api/v1/productos/{id}", id).retrieve().body(ProductoCatalogo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("el producto", id);
        }
    }

    public List<ProductoCatalogo> listarProductos() {
        return rest.get().uri("/api/v1/productos").retrieve().body(new ParameterizedTypeReference<>() {
        });
    }

    public List<CategoriaCatalogo> listarCategorias() {
        return rest.get().uri("/api/v1/categorias").retrieve().body(new ParameterizedTypeReference<>() {
        });
    }

    /** Descuenta el stock de los ítems de una orden pagada. Es "todo o nada" en catalogo-service. */
    public ResultadoStock descontarStock(List<DetalleOrden> items) {
        var cuerpo = new SolicitudStock(items.stream()
                .map(i -> new SolicitudStock.Item(i.getProductoId(), i.getCantidad()))
                .toList());
        return rest.post().uri("/internal/productos/descontar-stock")
                .header(ENCABEZADO_CLAVE, claveInterna)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cuerpo)
                .retrieve()
                .body(ResultadoStock.class);
    }

    public record SolicitudStock(List<Item> items) {
        public record Item(Long productoId, Integer cantidad) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResultadoStock(boolean ok, List<String> sinStock) {
    }
}
