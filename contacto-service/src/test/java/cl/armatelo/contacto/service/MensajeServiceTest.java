package cl.armatelo.contacto.service;

import cl.armatelo.contacto.dto.MensajeRequest;
import cl.armatelo.contacto.exception.RecursoNoEncontradoException;
import cl.armatelo.contacto.model.Mensaje;
import cl.armatelo.contacto.repository.MensajeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MensajeServiceTest {

    @Test
    @DisplayName("Guarda el mensaje con los textos limpios y el correo en minúsculas")
    void crear() {
        MensajeRepository repo = mock(MensajeRepository.class);
        when(repo.save(any(Mensaje.class))).thenAnswer(inv -> inv.getArgument(0));

        var r = new MensajeService(repo).crear(new MensajeRequest("  Ana ", "ANA@Correo.cl", " Consulta ", " Hola "));

        assertEquals("Ana", r.nombre());
        assertEquals("ana@correo.cl", r.correo());
        assertEquals("Consulta", r.asunto());
    }

    @Test
    @DisplayName("Eliminar un mensaje inexistente responde 404")
    void eliminarInexistente() {
        MensajeRepository repo = mock(MensajeRepository.class);
        when(repo.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> new MensajeService(repo).eliminar(99L));
    }
}
