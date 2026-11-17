package cl.armatelo.catalogo.service;

/** Normaliza textos opcionales: quita espacios y guarda los vacíos como null. */
final class Textos {

    private Textos() {
    }

    static String limpiar(String valor) {
        if (valor == null) return null;
        String t = valor.trim();
        return t.isEmpty() ? null : t;
    }

    static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
