package cl.armatelo.catalogo.controller;

import cl.armatelo.catalogo.dto.ProductoRequest;
import cl.armatelo.catalogo.dto.ProductoResponse;
import cl.armatelo.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo público; administración solo ADMIN")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Público. Todos los filtros son opcionales.")
    public List<ProductoResponse> listar(
            @Parameter(description = "Busca en nombre, marca y descripción") @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Precio final mínimo") @RequestParam(required = false) Integer min,
            @Parameter(description = "Precio final máximo") @RequestParam(required = false) Integer max,
            @Parameter(description = "precio-asc | precio-desc | nombre") @RequestParam(required = false) String orden) {
        return productoService.listar(texto, categoriaId, min, max, orden);
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Productos en oferta", description = "Público.")
    public List<ProductoResponse> ofertas() {
        return productoService.ofertas();
    }

    @GetMapping("/criticos")
    @Operation(summary = "Productos con stock crítico", description = "Solo ADMIN.")
    public List<ProductoResponse> criticos() {
        return productoService.criticos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de un producto", description = "Público.")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear producto", description = "Solo ADMIN.")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest req) {
        return productoService.crear(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar producto", description = "Solo ADMIN.")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return productoService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar producto", description = "Solo ADMIN. Las boletas conservan su copia del producto.")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
