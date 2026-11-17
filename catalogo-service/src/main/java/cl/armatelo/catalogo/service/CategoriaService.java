package cl.armatelo.catalogo.service;

import cl.armatelo.catalogo.dto.CategoriaRequest;
import cl.armatelo.catalogo.dto.CategoriaResponse;
import cl.armatelo.catalogo.exception.RecursoNoEncontradoException;
import cl.armatelo.catalogo.exception.ReglaNegocioException;
import cl.armatelo.catalogo.model.Categoria;
import cl.armatelo.catalogo.repository.CategoriaRepository;
import cl.armatelo.catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private static final String ICONO_POR_DEFECTO = "bi-tag";

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll(Sort.by("id")).stream().map(CategoriaResponse::desde).toList();
    }

    public CategoriaResponse obtener(Long id) {
        return CategoriaResponse.desde(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest req) {
        String nombre = req.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw ReglaNegocioException.conflicto("Ya existe una categoría con ese nombre.");
        }
        Categoria c = new Categoria(null, nombre, Textos.limpiar(req.descripcion()), icono(req.icono()));
        return CategoriaResponse.desde(categoriaRepository.save(c));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest req) {
        Categoria c = buscar(id);
        String nombre = req.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw ReglaNegocioException.conflicto("Ya existe una categoría con ese nombre.");
        }
        c.setNombre(nombre);
        c.setDescripcion(Textos.limpiar(req.descripcion()));
        c.setIcono(icono(req.icono()));
        return CategoriaResponse.desde(c);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria c = buscar(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw ReglaNegocioException.conflicto("No se puede eliminar: la categoría tiene productos asociados.");
        }
        categoriaRepository.delete(c);
    }

    Categoria buscar(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("la categoría", id));
    }

    private static String icono(String valor) {
        String limpio = Textos.limpiar(valor);
        return limpio == null ? ICONO_POR_DEFECTO : limpio;
    }
}
