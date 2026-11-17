package cl.armatelo.contacto.service;

import cl.armatelo.contacto.dto.MensajeRequest;
import cl.armatelo.contacto.dto.MensajeResponse;
import cl.armatelo.contacto.exception.RecursoNoEncontradoException;
import cl.armatelo.contacto.model.Mensaje;
import cl.armatelo.contacto.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MensajeService {

    private final MensajeRepository mensajeRepository;

    @Transactional
    public MensajeResponse crear(MensajeRequest req) {
        Mensaje m = new Mensaje();
        m.setNombre(req.nombre().trim());
        m.setCorreo(req.correo().trim().toLowerCase());
        m.setAsunto(req.asunto().trim());
        m.setMensaje(req.mensaje().trim());
        return MensajeResponse.desde(mensajeRepository.save(m));
    }

    public List<MensajeResponse> listar() {
        return mensajeRepository.findAllByOrderByFechaDesc().stream().map(MensajeResponse::desde).toList();
    }

    @Transactional
    public void eliminar(Long id) {
        if (!mensajeRepository.existsById(id)) throw new RecursoNoEncontradoException("el mensaje", id);
        mensajeRepository.deleteById(id);
    }
}
