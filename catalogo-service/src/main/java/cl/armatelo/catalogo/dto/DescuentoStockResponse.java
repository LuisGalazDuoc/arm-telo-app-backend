package cl.armatelo.catalogo.dto;

import java.util.List;

/** ok = true si se descontó todo; si no, "sinStock" trae los nombres de los productos sin unidades suficientes. */
public record DescuentoStockResponse(boolean ok, List<String> sinStock) {
}
