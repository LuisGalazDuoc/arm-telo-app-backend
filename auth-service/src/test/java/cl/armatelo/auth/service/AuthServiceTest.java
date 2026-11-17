package cl.armatelo.auth.service;

import cl.armatelo.auth.dto.LoginRequest;
import cl.armatelo.auth.dto.RegistroRequest;
import cl.armatelo.auth.exception.ReglaNegocioException;
import cl.armatelo.auth.model.Rol;
import cl.armatelo.auth.model.Usuario;
import cl.armatelo.auth.repository.UsuarioRepository;
import cl.armatelo.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioRepository repo;
    private AuthService servicio;

    @BeforeEach
    void preparar() {
        repo = mock(UsuarioRepository.class);
        servicio = new AuthService(repo, encoder, new JwtService("clave-de-pruebas-con-mas-de-32-caracteres!!", 60));
    }

    private Usuario admin() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Administrador");
        u.setApellidos("Armatelo");
        u.setCorreo("admin@armatelo.cl");
        u.setPassword(encoder.encode("Admin1234"));
        u.setRol(Rol.ADMIN);
        return u;
    }

    @Test
    @DisplayName("Login correcto devuelve token y el rol en minúsculas como espera el frontend")
    void loginCorrecto() {
        when(repo.findByCorreoIgnoreCase("admin@armatelo.cl")).thenReturn(Optional.of(admin()));

        var r = servicio.login(new LoginRequest("admin@armatelo.cl", "Admin1234"));

        assertFalse(r.token().isBlank());
        assertEquals("Bearer", r.tipo());
        assertEquals("admin", r.usuario().rol());
    }

    @Test
    @DisplayName("Contraseña incorrecta responde 401")
    void loginIncorrecto() {
        when(repo.findByCorreoIgnoreCase("admin@armatelo.cl")).thenReturn(Optional.of(admin()));

        var ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.login(new LoginRequest("admin@armatelo.cl", "otra-clave")));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    @DisplayName("El registro siempre crea un cliente y rechaza correos repetidos")
    void registro() {
        when(repo.existsByCorreoIgnoreCase("ana@correo.cl")).thenReturn(false);
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(9L);
            return u;
        });
        var req = new RegistroRequest("Ana", "Pérez", "ANA@correo.cl", "Clave1234", null, null, null, null, null);

        assertEquals("cliente", servicio.registrar(req).usuario().rol());

        when(repo.existsByCorreoIgnoreCase("ana@correo.cl")).thenReturn(true);
        var ex = assertThrows(ReglaNegocioException.class, () -> servicio.registrar(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }
}
