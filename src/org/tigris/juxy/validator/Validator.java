package org.tigris.juxy.validator;

import org.w3c.dom.Node;

/**
 * @author pavel
 */
public interface Validator {
  void validate(Node node) throws ValidationFailedException;
}
