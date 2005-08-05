package org.tigris.juxy;

/**
 */
public class JuxyRuntimeException extends RuntimeException {
    public JuxyRuntimeException(String message) {
        super(message);
    }

    public JuxyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
