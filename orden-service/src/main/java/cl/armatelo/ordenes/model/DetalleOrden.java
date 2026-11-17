package cl.armatelo.ordenes.model;

import cl.armatelo.ordenes.client.ProductoCatalogo;
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
 * Línea de una orden. Nombre, categoría y precio se copian al comprar,
 * así la boleta no cambia si después se edita o elimina el producto en catalogo-service.
 */
@Entity
@Table(name = "detalle_orden")
@Getter
@Setter
@NoArgsConstructor
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    /** Id del producto en catalogo-service. */
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(nullable = false, length = 150)
    private String nombre;

    /** Precio unitario cobrado (ya considera la oferta). */
    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer subtotal;

    public static DetalleOrden desde(ProductoCatalogo producto, int cantidad) {
        DetalleOrden d = new DetalleOrden();
        d.productoId = producto.id();
        d.categoriaId = producto.categoriaId();
        d.nombre = producto.nombre();
        d.precio = producto.precioFinal();
        d.cantidad = cantidad;
        d.subtotal = d.precio * cantidad;
        return d;
    }
}
