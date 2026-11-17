package cl.armatelo.ordenes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de quien compra, copiados en la orden (sirve también para invitados sin cuenta). */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatosCliente {

    @Column(name = "cliente_nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "cliente_apellidos", nullable = false, length = 120)
    private String apellidos;

    @Column(name = "cliente_correo", nullable = false, length = 120)
    private String correo;
}
