package cl.armatelo.ordenes.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger: http://localhost:9093/swagger-ui.html */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Armatelo - orden-service", version = "v1",
                description = "Checkout, pago simulado, boletas, historial de compras y reportes."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT",
        description = "Token obtenido en POST /api/v1/auth/login (auth-service)")
public class OpenApiConfig {
}
