package cl.armatelo.auth.model;

import java.util.Arrays;

/**
 * Roles del sistema (los mismos del frontend):
 * ADMIN tiene acceso total; CLIENTE solo accede a la tienda.
 * En la API se escriben en minúsculas ("admin", "cliente").
 */
public enum Rol {
    ADMIN, CLIENTE;

    public String valor() {
        return name().toLowerCase();
    }

    public static Rol desde(String texto) {
        if (texto == null || texto.isBlank()) return CLIENTE;
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(texto.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + texto));
    }
}
