package cl.armatelo.contacto.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger: http://localhost:9094/swagger-ui.html */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Armatelo - contacto-service", version = "v1",
                description = "Mensajes del formulario de contacto. Enviar es público; leer y eliminar solo admin."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT",
        description = "Token obtenido en POST /api/v1/auth/login (auth-service)")
public class OpenApiConfig {
}
