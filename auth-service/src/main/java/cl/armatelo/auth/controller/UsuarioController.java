package cl.armatelo.auth.controller;

import cl.armatelo.auth.dto.PerfilRequest;
import cl.armatelo.auth.dto.UsuarioRequest;
import cl.armatelo.auth.dto.UsuarioResponse;
import cl.armatelo.auth.security.UsuarioToken;
import cl.armatelo.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Perfil propio (autenticado) y gestión de cuentas (ADMIN)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Ver mi perfil", description = "Cualquier usuario autenticado.")
    public UsuarioResponse miPerfil(@AuthenticationPrincipal Jwt jwt) {
        return usuarioService.perfil(UsuarioToken.desde(jwt).id());
    }

    @PutMapping("/me")
    @Operation(summary = "Editar mi perfil", description = "Cualquier usuario autenticado. El correo y el rol no se modifican.")
    public UsuarioResponse editarMiPerfil(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PerfilRequest req) {
        return usuarioService.actualizarPerfil(UsuarioToken.desde(jwt).id(), req);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Solo ADMIN.")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario", description = "Solo ADMIN.")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario", description = "Solo ADMIN. Rol admin o cliente.")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest req) {
        return usuarioService.crear(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar usuario", description = "Solo ADMIN. Si la contraseña viene vacía se mantiene la actual.")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return usuarioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario", description = "Solo ADMIN. No se puede eliminar la propia cuenta.")
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        usuarioService.eliminar(id, UsuarioToken.desde(jwt).id());
    }
}
