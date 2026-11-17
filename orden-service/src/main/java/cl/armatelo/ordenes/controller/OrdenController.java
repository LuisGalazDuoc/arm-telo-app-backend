package cl.armatelo.ordenes.controller;

import cl.armatelo.ordenes.dto.OrdenRequest;
import cl.armatelo.ordenes.dto.OrdenResponse;
import cl.armatelo.ordenes.dto.PagoRequest;
import cl.armatelo.ordenes.dto.PagoResponse;
import cl.armatelo.ordenes.security.UsuarioToken;
import cl.armatelo.ordenes.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
@Tag(name = "Órdenes", description = "Checkout y pago (público), mis compras (autenticado), consulta de órdenes (ADMIN)")
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear orden (checkout)",
            description = "Público (invitado o cliente con token). Valida stock y calcula totales en el servidor. Queda pendiente.")
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest req, @AuthenticationPrincipal Jwt jwt) {
        return ordenService.crear(req, UsuarioToken.desde(jwt));
    }

    @PostMapping("/{id}/pago")
    @Operation(summary = "Pagar una orden (simulación)",
            description = "Público. resultado = aprobado | rechazado. Si se aprueba, se descuenta el stock en catalogo-service.")
    public PagoResponse pagar(@PathVariable Long id, @Valid @RequestBody(required = false) PagoRequest req) {
        return ordenService.pagar(id, req);
    }

    @GetMapping
    @Operation(summary = "Listar órdenes", description = "Solo ADMIN. Filtros opcionales: estado, o usuarioId/correo para el historial de un usuario.")
    public List<OrdenResponse> listar(
            @Parameter(description = "pendiente | pagada | rechazada") @RequestParam(required = false) String estado,
            @Parameter(description = "Historial: id del usuario") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Historial: correo del usuario") @RequestParam(required = false) String correo) {
        return ordenService.listar(estado, usuarioId, correo);
    }

    @GetMapping("/mias")
    @Operation(summary = "Mis compras", description = "Usuario autenticado.")
    public List<OrdenResponse> mias(@AuthenticationPrincipal Jwt jwt) {
        return ordenService.mias(UsuarioToken.desde(jwt));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una orden (boleta)",
            description = "ADMIN siempre; un cliente solo las suyas; un invitado indicando el correo de la compra.")
    public OrdenResponse obtener(@PathVariable Long id,
                                 @AuthenticationPrincipal Jwt jwt,
                                 @Parameter(description = "Correo usado en la compra (solo invitados)")
                                 @RequestParam(required = false) String correo) {
        return ordenService.obtener(id, UsuarioToken.desde(jwt), correo);
    }
}
