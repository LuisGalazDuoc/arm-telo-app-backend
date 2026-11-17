package cl.armatelo.contacto.dto;

import cl.armatelo.contacto.model.Mensaje;

import java.time.LocalDateTime;

public record MensajeResponse(Long id, String nombre, String correo, String asunto, String mensaje, LocalDateTime fecha) {

    public static MensajeResponse desde(Mensaje m) {
        return new MensajeResponse(m.getId(), m.getNombre(), m.getCorreo(), m.getAsunto(), m.getMensaje(), m.getFecha());
    }
}
