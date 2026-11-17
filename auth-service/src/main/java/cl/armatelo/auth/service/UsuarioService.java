package cl.armatelo.auth.service;

import cl.armatelo.auth.dto.PerfilRequest;
import cl.armatelo.auth.dto.UsuarioRequest;
import cl.armatelo.auth.dto.UsuarioResponse;
import cl.armatelo.auth.exception.RecursoNoEncontradoException;
import cl.armatelo.auth.exception.ReglaNegocioException;
import cl.armatelo.auth.model.Rol;
import cl.armatelo.auth.model.Usuario;
import cl.armatelo.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestión de cuentas (Admin › Usuarios) y perfil propio (Perfil / Admin › Perfil).
 * El historial de compras de un usuario lo entrega orden-service (GET /api/v1/ordenes?usuarioId=&correo=).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    static final int MIN_PASSWORD = 8;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------- Administración (ADMIN) ----------

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by("id")).stream().map(UsuarioResponse::desde).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest req) {
        String correo = req.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ReglaNegocioException.conflicto("Ya existe una cuenta con ese correo.");
        }
        if (Textos.vacio(req.password())) {
            throw new ReglaNegocioException("Ingresa una contraseña.");
        }
        Usuario u = new Usuario();
        aplicar(u, req, correo);
        u.setPassword(cifrar(req.password()));
        return UsuarioResponse.desde(usuarioRepository.save(u));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest req) {
        Usuario u = buscar(id);
        String correo = req.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw ReglaNegocioException.conflicto("Ya existe una cuenta con ese correo.");
        }
        aplicar(u, req, correo);
        if (!Textos.vacio(req.password())) u.setPassword(cifrar(req.password())); // vacía = se mantiene
        return UsuarioResponse.desde(u);
    }

    @Transactional
    public void eliminar(Long id, Long idSolicitante) {
        if (id.equals(idSolicitante)) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta.");
        }
        usuarioRepository.delete(buscar(id)); // sus órdenes se conservan en orden-service
    }

    public long total() {
        return usuarioRepository.count();
    }

    // ---------- Perfil propio ----------

    public UsuarioResponse perfil(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(Long id, PerfilRequest req) {
        Usuario u = buscar(id);
        u.setNombre(req.nombre().trim());
        u.setApellidos(req.apellidos().trim());
        u.setTelefono(Textos.limpiar(req.telefono()));
        u.setRegion(Textos.limpiar(req.region()));
        u.setComuna(Textos.limpiar(req.comuna()));
        u.setCalle(Textos.limpiar(req.calle()));
        u.setDepartamento(Textos.limpiar(req.departamento()));
        if (!Textos.vacio(req.password())) u.setPassword(cifrar(req.password()));
        return UsuarioResponse.desde(u);
    }

    // ---------- Apoyo ----------

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("el usuario", id));
    }

    private void aplicar(Usuario u, UsuarioRequest req, String correo) {
        u.setNombre(req.nombre().trim());
        u.setApellidos(req.apellidos().trim());
        u.setCorreo(correo);
        u.setRol(Rol.desde(req.rol()));
        u.setTelefono(Textos.limpiar(req.telefono()));
        u.setRegion(Textos.limpiar(req.region()));
        u.setComuna(Textos.limpiar(req.comuna()));
        u.setCalle(Textos.limpiar(req.calle()));
        u.setDepartamento(Textos.limpiar(req.departamento()));
    }

    private String cifrar(String password) {
        if (password.length() < MIN_PASSWORD) {
            throw new ReglaNegocioException("La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres.");
        }
        return passwordEncoder.encode(password);
    }
}
