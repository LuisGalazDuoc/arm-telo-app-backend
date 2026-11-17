package cl.armatelo.ordenes.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Seguridad de orden-service: valida el JWT emitido por auth-service (OAuth2 Resource Server, HS256)
 * y convierte el claim "rol" en ROLE_ADMIN / ROLE_CLIENTE.
 *
 * <pre>
 * Público ...... POST /ordenes (checkout, también invitados), POST /ordenes/{id}/pago, GET /ordenes/{id} (el servicio valida el acceso), Swagger
 * Autenticado .. GET /ordenes/mias
 * ADMIN ........ GET /ordenes (lista e historial) y /reportes
 * </pre>
 */
@Configuration
public class SecurityConfig {

    private static final String API = "/api/v1";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        AuthenticationEntryPoint noAutenticado = (req, res, ex) -> escribirError(res, 401, "Unauthorized",
                "Debes iniciar sesión (token JWT ausente, inválido o expirado).", req.getRequestURI());
        AccessDeniedHandler sinPermiso = (req, res, ex) -> escribirError(res, 403, "Forbidden",
                "No tienes permisos para realizar esta acción.", req.getRequestURI());

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, API + "/ordenes/mias").authenticated()
                        .requestMatchers(HttpMethod.GET, API + "/ordenes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, API + "/ordenes/*").permitAll() // el servicio valida quién puede verla
                        .requestMatchers(HttpMethod.POST, API + "/ordenes", API + "/ordenes/*/pago").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .oauth2ResourceServer(o -> o
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(convertidorDeRoles()))
                        .authenticationEntryPoint(noAutenticado)
                        .accessDeniedHandler(sinPermiso))
                .exceptionHandling(e -> e.authenticationEntryPoint(noAutenticado).accessDeniedHandler(sinPermiso));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${armatelo.jwt.secret}") String secreto) {
        return decodificador(secreto);
    }

    /** Decodificador HS256 con la clave compartida (público para poder probarlo). */
    public static JwtDecoder decodificador(String secreto) {
        SecretKeySpec clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /** "rol": "ADMIN" → autoridad ROLE_ADMIN (lo que exige hasRole("ADMIN")). */
    public static JwtAuthenticationConverter convertidorDeRoles() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(roles);
        return convertidor;
    }

    private static void escribirError(HttpServletResponse res, int status, String error, String mensaje, String ruta) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("""
                {"status":%d,"error":"%s","mensaje":"%s","ruta":"%s","campos":null,"fecha":"%s"}"""
                .formatted(status, error, mensaje, ruta.replace("\"", ""), LocalDateTime.now()));
    }
}
