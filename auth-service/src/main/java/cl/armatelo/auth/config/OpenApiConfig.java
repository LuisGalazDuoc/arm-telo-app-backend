package cl.armatelo.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger: http://localhost:9091/swagger-ui.html */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Armatelo - auth-service", version = "v1",
                description = "Login, registro (JWT), perfil y gestión de usuarios. Roles: admin y cliente."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT",
        description = "Token obtenido en POST /api/v1/auth/login")
public class OpenApiConfig {
}
