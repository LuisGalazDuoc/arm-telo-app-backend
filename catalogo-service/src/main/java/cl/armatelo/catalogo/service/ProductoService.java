package cl.armatelo.catalogo.service;

import cl.armatelo.catalogo.dto.DescuentoStockRequest;
import cl.armatelo.catalogo.dto.DescuentoStockResponse;
import cl.armatelo.catalogo.dto.ProductoRequest;
import cl.armatelo.catalogo.dto.ProductoResponse;
import cl.armatelo.catalogo.exception.RecursoNoEncontradoException;
import cl.armatelo.catalogo.exception.ReglaNegocioException;
import cl.armatelo.catalogo.model.Producto;
import cl.armatelo.catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    /**
     * Catálogo con los mismos filtros de la página Productos del frontend.
     *
     * @param orden "precio-asc", "precio-desc" o "nombre"
     */
    public List<ProductoResponse> listar(String texto, Long categoriaId, Integer min, Integer max, String orden) {
        return filtrar(productoRepository.findAllByOrderByIdAsc(), texto, categoriaId, min, max, orden)
                .stream().map(ProductoResponse::desde).toList();
    }

    /** Página Ofertas. */
    public List<ProductoResponse> ofertas() {
        return productoRepository.findAllByOrderByIdAsc().stream()
                .filter(Producto::enOferta).map(ProductoResponse::desde).toList();
    }

    /** Admin › Productos críticos: stock igual o menor a su stock crítico. */
    public List<ProductoResponse> criticos() {
        return productoRepository.findAllByOrderByIdAsc().stream()
                .filter(Producto::esCritico).map(ProductoResponse::desde).toList();
    }

    public ProductoResponse obtener(Long id) {
        return ProductoResponse.desde(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest req) {
        Producto p = new Producto();
        aplicar(p, req);
        return ProductoResponse.desde(productoRepository.save(p));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        Producto p = buscar(id);
        aplicar(p, req);
        return ProductoResponse.desde(p);
    }

    /** Las boletas de orden-service guardan su propia copia del producto, por eso se puede eliminar. */
    @Transactional
    public void eliminar(Long id) {
        productoRepository.delete(buscar(id));
    }

    /**
     * Descuenta el stock de un pago aprobado (lo llama orden-service).
     * Es "todo o nada": si algún producto no alcanza, no se descuenta ninguno.
     */
    @Transactional
    public DescuentoStockResponse descontarStock(DescuentoStockRequest req) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        req.items().forEach(i -> cantidades.merge(i.productoId(), i.cantidad(), Integer::sum));

        Map<Long, Producto> productos = productoRepository.findByIdIn(cantidades.keySet()).stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));

        List<String> sinStock = new ArrayList<>();
        cantidades.forEach((id, cantidad) -> {
            Producto p = productos.get(id);
            if (p == null) sinStock.add("Producto #" + id + " (ya no existe)");
            else if (cantidad > p.stockNumerico()) sinStock.add(p.getNombre());
        });
        if (!sinStock.isEmpty()) return new DescuentoStockResponse(false, sinStock);

        cantidades.forEach((id, cantidad) -> productos.get(id).descontarStock(cantidad));
        return new DescuentoStockResponse(true, List.of());
    }

    // ---------- Reglas ----------

    /** Filtrado y orden (función pura, se prueba sin base de datos). */
    static List<Producto> filtrar(List<Producto> lista, String texto, Long categoriaId, Integer min, Integer max, String orden) {
        String buscado = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        Predicate<Producto> coincide = p -> {
            if (!buscado.isEmpty()) {
                String contenido = (p.getNombre() + " " + nulo(p.getMarca()) + " " + nulo(p.getDescripcion())).toLowerCase(Locale.ROOT);
                if (!contenido.contains(buscado)) return false;
            }
            if (categoriaId != null && !categoriaId.equals(p.getCategoria().getId())) return false;
            if (min != null && p.precioFinal() < min) return false;
            if (max != null && p.precioFinal() > max) return false;
            return true;
        };
        List<Producto> resultado = lista.stream().filter(coincide).toList();
        Comparator<Producto> comparador = comparador(orden);
        return comparador == null ? resultado : resultado.stream().sorted(comparador).toList();
    }

    private static Comparator<Producto> comparador(String orden) {
        if (orden == null) return null;
        Collator collator = Collator.getInstance(Locale.of("es"));
        return switch (orden) {
            case "precio-asc" -> Comparator.comparingInt(Producto::precioFinal);
            case "precio-desc" -> Comparator.comparingInt(Producto::precioFinal).reversed();
            case "nombre" -> Comparator.comparing(Producto::getNombre, collator);
            default -> null;
        };
    }

    private void aplicar(Producto p, ProductoRequest req) {
        if (req.precioOferta() != null && req.precioOferta() >= req.precio()) {
            throw new ReglaNegocioException("El precio de oferta debe ser menor al precio normal.");
        }
        p.setNombre(req.nombre().trim());
        p.setDescripcion(Textos.limpiar(req.descripcion()));
        p.setMarca(Textos.limpiar(req.marca()));
        p.setCategoria(categoriaService.buscar(req.categoriaId()));
        p.setPrecio(req.precio());
        p.setPrecioOferta(req.precioOferta());
        p.setStock(req.stock());
        p.setStockCritico(req.stockCritico());
        p.setImagen(Textos.limpiar(req.imagen()));
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el producto", id));
    }

    private static String nulo(String s) {
        return s == null ? "" : s;
    }
}
