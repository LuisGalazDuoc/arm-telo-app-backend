package cl.armatelo.ordenes.controller;

import cl.armatelo.ordenes.dto.ReporteResponse;
import cl.armatelo.ordenes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Ventas e inventario (solo ADMIN)")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    @Operation(summary = "Resumen de ventas e inventario", description = "Solo ADMIN.")
    public ReporteResponse resumen() {
        return reporteService.resumen();
    }

    @GetMapping(value = "/ordenes.csv", produces = "text/csv")
    @Operation(summary = "Descargar órdenes en CSV", description = "Solo ADMIN.")
    public ResponseEntity<String> ordenesCsv() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ordenes-armatelo.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(reporteService.ordenesCsv());
    }
}
