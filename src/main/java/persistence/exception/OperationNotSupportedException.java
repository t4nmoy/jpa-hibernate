package persistence.exception;

import org.springframework.core.NestedRuntimeException;

public class OperationNotSupportedException extends NestedRuntimeException {

    public OperationNotSupportedException(String msg) {
        super(msg);
    }

}
