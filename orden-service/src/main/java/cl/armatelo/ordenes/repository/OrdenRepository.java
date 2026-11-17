package cl.armatelo.ordenes.repository;

import cl.armatelo.ordenes.model.EstadoOrden;
import cl.armatelo.ordenes.model.Orden;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @EntityGraph(attributePaths = "items")
    List<Orden> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = "items")
    List<Orden> findByEstadoOrderByIdDesc(EstadoOrden estado);

    /** Historial de una persona: órdenes hechas con su cuenta o con su correo (ordenesDeUsuario() en el frontend). */
    @EntityGraph(attributePaths = "items")
    @Query("""
            select o from Orden o
            where (:usuarioId is not null and o.usuarioId = :usuarioId)
               or (:correo is not null and lower(o.cliente.correo) = lower(:correo))
            order by o.id desc""")
    List<Orden> buscarHistorial(@Param("usuarioId") Long usuarioId, @Param("correo") String correo);
}
