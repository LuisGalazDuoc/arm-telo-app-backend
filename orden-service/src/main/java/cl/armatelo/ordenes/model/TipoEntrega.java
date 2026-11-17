package cl.armatelo.ordenes.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/** Opciones de entrega del checkout (OPCIONES_ENTREGA en src/services/compras.js). */
@Getter
@RequiredArgsConstructor
public enum TipoEntrega {
    DESPACHO(3990), RETIRO(0);

    private final int costo;

    public String valor() {
        return name().toLowerCase();
    }

    public static TipoEntrega desde(String texto) {
        if (texto == null || texto.isBlank()) return RETIRO;
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(texto.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de entrega no válido: " + texto));
    }
}
