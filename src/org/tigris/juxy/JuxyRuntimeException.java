package org.tigris.juxy;

/**
 * $Id: JuxyRuntimeException.java,v 1.2 2005-08-05 08:38:29 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class JuxyRuntimeException extends RuntimeException {
    public JuxyRuntimeException(String message) {
        super(message);
    }

    public JuxyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
