package cl.armatelo.ordenes.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Orden de compra (boleta). Guarda una copia de los productos y precios del momento. */
@Entity
@Table(name = "ordenes")
@Getter
@Setter
@NoArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número visible de la boleta: fecha (yyyyMMdd) + id, por ejemplo 2026111607. */
    @Column(length = 30)
    private String numero;

    /** Id del usuario en auth-service; null si se compró como invitado. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Embedded
    private DatosCliente cliente;

    @Embedded
    private Direccion direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEntrega entrega;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<DetalleOrden> items = new ArrayList<>();

    @Column(nullable = false)
    private Integer subtotal;

    @Column(nullable = false)
    private Integer envio;

    @Column(nullable = false)
    private Integer total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    @Column(length = 300)
    private String motivo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @PrePersist
    void alCrear() {
        if (fecha == null) fecha = LocalDateTime.now();
    }

    public void agregarItem(DetalleOrden item) {
        item.setOrden(this);
        items.add(item);
    }

    /** Código visible, por ejemplo ORDER00012 (codigoOrden() en el frontend). */
    public String codigo() {
        return "ORDER%05d".formatted(id);
    }
}
