package org.tigris.juxy.validator;

import org.w3c.dom.Node;

/**
 * User: pavel
 * Date: 10.10.2006
 */
public interface Validator {
  void validate(Node node) throws ValidationFailedException;
}
