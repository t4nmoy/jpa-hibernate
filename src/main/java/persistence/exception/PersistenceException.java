package persistence.exception;

import org.springframework.core.NestedRuntimeException;

public class PersistenceException extends NestedRuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
