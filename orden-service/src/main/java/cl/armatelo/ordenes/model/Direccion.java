package cl.armatelo.ordenes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dirección de despacho (vacía cuando es retiro en tienda). */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {

    @Column(name = "dir_calle", length = 150)
    private String calle;

    @Column(name = "dir_departamento", length = 60)
    private String departamento;

    @Column(name = "dir_region", length = 80)
    private String region;

    @Column(name = "dir_comuna", length = 80)
    private String comuna;

    @Column(name = "dir_indicaciones", length = 300)
    private String indicaciones;
}
