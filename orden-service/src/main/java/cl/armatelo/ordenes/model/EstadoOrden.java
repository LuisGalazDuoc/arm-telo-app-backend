package cl.armatelo.ordenes.model;

import java.util.Arrays;

/** Una orden se crea PENDIENTE y el pago la deja PAGADA o RECHAZADA (puede reintentarse). */
public enum EstadoOrden {
    PENDIENTE, PAGADA, RECHAZADA;

    public String valor() {
        return name().toLowerCase();
    }

    public static EstadoOrden desde(String texto) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(texto.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado no válido: " + texto));
    }
}
