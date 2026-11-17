package cl.armatelo.contacto.repository;

import cl.armatelo.contacto.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findAllByOrderByFechaDesc();
}
