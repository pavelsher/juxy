package org.tigris.juxy.validator;

/**
 * @author pavel
 */
public class ValidationFailedException extends AssertionError {

  public ValidationFailedException(String message) {
    super(message);
  }
}
