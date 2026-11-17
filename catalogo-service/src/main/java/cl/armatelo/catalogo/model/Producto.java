package cl.armatelo.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Producto de la tienda. Incluye las mismas reglas que el frontend (src/utils/productos.js):
 * stock sin definir, ofertas, precio final y stock crítico.
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 80)
    private String marca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /** Precio normal en pesos chilenos. */
    @Column(nullable = false)
    private Integer precio;

    /** Precio rebajado; null si no está en oferta. */
    @Column(name = "precio_oferta")
    private Integer precioOferta;

    /** Unidades disponibles; null = "stock por definir" (cuenta como 0). */
    private Integer stock;

    @Column(name = "stock_critico", nullable = false)
    private Integer stockCritico = 5;

    @Column(length = 500)
    private String imagen;

    // ---------- Reglas de negocio ----------

    public int stockNumerico() {
        return stock == null || stock < 0 ? 0 : stock;
    }

    public boolean enOferta() {
        return precioOferta != null && precioOferta > 0 && precio != null && precioOferta < precio;
    }

    public int precioFinal() {
        if (enOferta()) return precioOferta;
        return precio == null ? 0 : precio;
    }

    public boolean esCritico() {
        int critico = stockCritico == null ? 0 : stockCritico;
        return stockNumerico() <= critico;
    }

    /** Descuenta unidades vendidas. Si el stock no está definido no se modifica. */
    public void descontarStock(int cantidad) {
        if (stock != null) stock = Math.max(0, stock - cantidad);
    }
}
