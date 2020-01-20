package persistence.exception;

public class NoSuchEntityException extends PersistenceException {

    private static final long serialVersionUID = 1L;

    public NoSuchEntityException(String msg) {
        super(msg);
    }

    public NoSuchEntityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
