package cl.armatelo.catalogo.controller;

import cl.armatelo.catalogo.dto.DescuentoStockRequest;
import cl.armatelo.catalogo.dto.DescuentoStockResponse;
import cl.armatelo.catalogo.security.ClaveInterna;
import cl.armatelo.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints para otros microservicios (no publicados en el gateway). */
@Hidden
@RestController
@RequestMapping("/internal/productos")
@RequiredArgsConstructor
public class InternoController {

    private final ProductoService productoService;
    private final ClaveInterna claveInterna;

    /** orden-service lo llama al aprobar un pago. */
    @PostMapping("/descontar-stock")
    public DescuentoStockResponse descontarStock(@RequestHeader(value = ClaveInterna.ENCABEZADO, required = false) String clave,
                                                 @Valid @RequestBody DescuentoStockRequest req) {
        claveInterna.validar(clave);
        return productoService.descontarStock(req);
    }
}
