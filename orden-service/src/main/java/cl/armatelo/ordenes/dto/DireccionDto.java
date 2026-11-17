package cl.armatelo.ordenes.dto;

import cl.armatelo.ordenes.model.Direccion;

/** Obligatoria (calle, región y comuna) solo cuando la entrega es "despacho". */
public record DireccionDto(String calle, String departamento, String region, String comuna, String indicaciones) {

    public static DireccionDto desde(Direccion d) {
        if (d == null || d.getCalle() == null) return null;
        return new DireccionDto(d.getCalle(), d.getDepartamento(), d.getRegion(), d.getComuna(), d.getIndicaciones());
    }
}
