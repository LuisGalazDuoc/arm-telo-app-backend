package cl.armatelo.auth.controller;

import cl.armatelo.auth.dto.AuthResponse;
import cl.armatelo.auth.dto.LoginRequest;
import cl.armatelo.auth.dto.RegistroRequest;
import cl.armatelo.auth.dto.UsuarioResponse;
import cl.armatelo.auth.security.UsuarioToken;
import cl.armatelo.auth.service.AuthService;
import cl.armatelo.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, registro y sesión actual (JWT)")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Público. Devuelve el token JWT y los datos del usuario.")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrarse como cliente", description = "Público. Crea una cuenta con rol cliente y devuelve su token.")
    public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
        return authService.registrar(req);
    }

    @GetMapping("/me")
    @Operation(summary = "Usuario de la sesión actual", description = "Requiere token.")
    public UsuarioResponse yo(@AuthenticationPrincipal Jwt jwt) {
        return usuarioService.perfil(UsuarioToken.desde(jwt).id());
    }
}
