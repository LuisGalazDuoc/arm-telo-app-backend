package cl.armatelo.contacto.exception;

/** Se traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("No encontramos " + recurso + " con id " + id + ".");
    }
}
