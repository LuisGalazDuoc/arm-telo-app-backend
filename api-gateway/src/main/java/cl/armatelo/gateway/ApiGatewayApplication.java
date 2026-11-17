package cl.armatelo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada único del frontend (http://localhost:9090). 
 * Las rutas hacia cada microservicio están en application.properties.
 * La seguridad (JWT y roles) la valida cada microservicio.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
