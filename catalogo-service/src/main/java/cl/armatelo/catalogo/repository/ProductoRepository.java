package cl.armatelo.catalogo.repository;

import cl.armatelo.catalogo.model.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Trae los productos junto a su categoría en una sola consulta. */
    @EntityGraph(attributePaths = "categoria")
    List<Producto> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "categoria")
    List<Producto> findByIdIn(Collection<Long> ids);

    boolean existsByCategoriaId(Long categoriaId);
}
