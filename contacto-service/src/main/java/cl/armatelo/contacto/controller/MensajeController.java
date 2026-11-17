package cl.armatelo.contacto.controller;

import cl.armatelo.contacto.dto.MensajeRequest;
import cl.armatelo.contacto.dto.MensajeResponse;
import cl.armatelo.contacto.service.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mensajes")
@RequiredArgsConstructor
@Tag(name = "Contacto", description = "Mensajes del formulario de contacto")
public class MensajeController {

    private final MensajeService mensajeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enviar mensaje de contacto", description = "Público.")
    public MensajeResponse crear(@Valid @RequestBody MensajeRequest req) {
        return mensajeService.crear(req);
    }

    @GetMapping
    @Operation(summary = "Listar mensajes", description = "Solo ADMIN.")
    public List<MensajeResponse> listar() {
        return mensajeService.listar();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar mensaje", description = "Solo ADMIN.")
    public void eliminar(@PathVariable Long id) {
        mensajeService.eliminar(id);
    }
}
