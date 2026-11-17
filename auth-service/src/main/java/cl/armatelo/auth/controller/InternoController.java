package cl.armatelo.auth.controller;

import cl.armatelo.auth.security.ClaveInterna;
import cl.armatelo.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Endpoints para otros microservicios (no publicados en el gateway). Lo usa orden-service para los reportes. */
@Hidden
@RestController
@RequestMapping("/internal/usuarios")
@RequiredArgsConstructor
public class InternoController {

    private final UsuarioService usuarioService;
    private final ClaveInterna claveInterna;

    @GetMapping("/total")
    public Map<String, Long> total(@RequestHeader(value = ClaveInterna.ENCABEZADO, required = false) String clave) {
        claveInterna.validar(clave);
        return Map.of("total", usuarioService.total());
    }
}
