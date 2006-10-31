package org.tigris.juxy;

/**
 * <p/>
 *
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
