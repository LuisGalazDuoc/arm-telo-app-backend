package cl.armatelo.auth.service;

import cl.armatelo.auth.dto.AuthResponse;
import cl.armatelo.auth.dto.LoginRequest;
import cl.armatelo.auth.dto.RegistroRequest;
import cl.armatelo.auth.dto.UsuarioResponse;
import cl.armatelo.auth.exception.ReglaNegocioException;
import cl.armatelo.auth.model.Rol;
import cl.armatelo.auth.model.Usuario;
import cl.armatelo.auth.repository.UsuarioRepository;
import cl.armatelo.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Inicio de sesión y registro (páginas Login y Registro). Ambos devuelven un JWT. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(req.correo().trim())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPassword()))
                .orElseThrow(() -> new ReglaNegocioException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        return respuesta(usuario);
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest req) {
        String correo = req.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ReglaNegocioException.conflicto("Ya existe una cuenta con ese correo.");
        }
        Usuario u = new Usuario();
        u.setNombre(req.nombre().trim());
        u.setApellidos(req.apellidos().trim());
        u.setCorreo(correo);
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRol(Rol.CLIENTE); // el registro público nunca crea administradores
        u.setTelefono(Textos.limpiar(req.telefono()));
        u.setRegion(Textos.limpiar(req.region()));
        u.setComuna(Textos.limpiar(req.comuna()));
        u.setCalle(Textos.limpiar(req.calle()));
        u.setDepartamento(Textos.limpiar(req.departamento()));
        return respuesta(usuarioRepository.save(u));
    }

    private AuthResponse respuesta(Usuario usuario) {
        return AuthResponse.bearer(jwtService.generarToken(usuario), jwtService.getExpiracionMinutos(), UsuarioResponse.desde(usuario));
    }
}
