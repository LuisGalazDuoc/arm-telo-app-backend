package cl.armatelo.catalogo.dto;

import cl.armatelo.catalogo.model.Categoria;

public record CategoriaResponse(Long id, String nombre, String descripcion, String icono) {

    public static CategoriaResponse desde(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), c.getIcono());
    }
}
