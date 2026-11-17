package cl.armatelo.catalogo.service;

import cl.armatelo.catalogo.dto.DescuentoStockRequest;
import cl.armatelo.catalogo.dto.ProductoRequest;
import cl.armatelo.catalogo.exception.ReglaNegocioException;
import cl.armatelo.catalogo.model.Categoria;
import cl.armatelo.catalogo.model.Producto;
import cl.armatelo.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductoServiceTest {

    private static Producto producto(long id, String nombre, String marca, long categoriaId, int precio, Integer oferta, Integer stock) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setMarca(marca);
        p.setCategoria(new Categoria(categoriaId, "Cat " + categoriaId, null, null));
        p.setPrecio(precio);
        p.setPrecioOferta(oferta);
        p.setStock(stock);
        return p;
    }

    private final List<Producto> catalogo = List.of(
            producto(1, "Ryzen 5 5600", "AMD", 1, 129990, 114990, 10),
            producto(2, "Core i5-12400F", "Intel", 1, 139990, null, 10),
            producto(3, "RTX 4060", "NVIDIA", 2, 349990, null, 10));

    @Test
    @DisplayName("Filtra por texto, categoría y precio final; ordena por precio")
    void filtros() {
        assertEquals(List.of(2L), ids(ProductoService.filtrar(catalogo, "intel", null, null, null, null)));
        assertEquals(List.of(1L, 2L), ids(ProductoService.filtrar(catalogo, null, 1L, null, null, null)));
        assertEquals(List.of(1L), ids(ProductoService.filtrar(catalogo, null, null, null, 120000, null)));
        assertEquals(List.of(3L, 2L, 1L), ids(ProductoService.filtrar(catalogo, null, null, null, null, "precio-desc")));
    }

    @Test
    @DisplayName("Rechaza un precio de oferta mayor o igual al precio normal")
    void ofertaInvalida() {
        ProductoRepository repo = mock(ProductoRepository.class);
        var servicio = new ProductoService(repo, mock(CategoriaService.class));
        var req = new ProductoRequest("X", null, null, 1L, 1000, 1000, 5, 1, null);

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(req));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Descontar stock es todo o nada")
    void descontarStock() {
        Producto ryzen = producto(1, "Ryzen 5 5600", "AMD", 1, 129990, null, 3);
        Producto rtx = producto(3, "RTX 4060", "NVIDIA", 2, 349990, null, 1);
        ProductoRepository repo = mock(ProductoRepository.class);
        when(repo.findByIdIn(anyCollection())).thenReturn(List.of(ryzen, rtx));
        var servicio = new ProductoService(repo, mock(CategoriaService.class));

        var sinStock = servicio.descontarStock(new DescuentoStockRequest(List.of(
                new DescuentoStockRequest.Item(1L, 2), new DescuentoStockRequest.Item(3L, 2))));
        assertFalse(sinStock.ok());
        assertEquals(List.of("RTX 4060"), sinStock.sinStock());
        assertEquals(3, ryzen.getStock()); // no se tocó nada

        var ok = servicio.descontarStock(new DescuentoStockRequest(List.of(
                new DescuentoStockRequest.Item(1L, 2), new DescuentoStockRequest.Item(3L, 1))));
        assertTrue(ok.ok());
        assertEquals(1, ryzen.getStock());
        assertEquals(0, rtx.getStock());
    }

    private static List<Long> ids(List<Producto> lista) {
        return lista.stream().map(Producto::getId).toList();
    }
}
