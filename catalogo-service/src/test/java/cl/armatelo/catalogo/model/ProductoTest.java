package cl.armatelo.catalogo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Reglas del producto (equivalentes a src/utils/productos.js del frontend). */
class ProductoTest {

    private static Producto producto(Integer precio, Integer oferta, Integer stock, Integer critico) {
        Producto p = new Producto();
        p.setNombre("Ryzen 5 5600");
        p.setPrecio(precio);
        p.setPrecioOferta(oferta);
        p.setStock(stock);
        p.setStockCritico(critico);
        return p;
    }

    @Test
    @DisplayName("Un stock sin definir cuenta como 0 unidades")
    void stockSinDefinir() {
        assertEquals(0, producto(1000, null, null, 5).stockNumerico());
    }

    @Test
    @DisplayName("La oferta solo aplica si es mayor a 0 y menor al precio normal")
    void oferta() {
        assertTrue(producto(1000, 800, 1, 0).enOferta());
        assertFalse(producto(1000, 1000, 1, 0).enOferta());
        assertFalse(producto(1000, 0, 1, 0).enOferta());
        assertEquals(800, producto(1000, 800, 1, 0).precioFinal());
        assertEquals(1000, producto(1000, null, 1, 0).precioFinal());
    }

    @Test
    @DisplayName("Es crítico cuando el stock es igual o menor al stock crítico")
    void critico() {
        assertTrue(producto(1000, null, 5, 5).esCritico());
        assertTrue(producto(1000, null, null, 5).esCritico());
        assertFalse(producto(1000, null, 6, 5).esCritico());
    }

    @Test
    @DisplayName("Descontar stock no baja de 0 y no cambia un stock sin definir")
    void descontar() {
        Producto p = producto(1000, null, 3, 0);
        p.descontarStock(5);
        assertEquals(0, p.getStock());

        Producto sinDefinir = producto(1000, null, null, 0);
        sinDefinir.descontarStock(1);
        assertNull(sinDefinir.getStock());
    }
}
