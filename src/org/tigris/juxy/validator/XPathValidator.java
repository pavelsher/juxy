package org.tigris.juxy.validator;

import org.w3c.dom.Node;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.tigris.juxy.xpath.XPathAssert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: pavel
 * Date: 14.10.2006
 */
public class XPathValidator implements Validator {
  private final static Log LOG = LogFactory.getLog(XPathValidator.class);
  private XPathAssert[] assertions;

  public XPathValidator(final XPathAssert[] assertions) {
    this.assertions = assertions;
  }

  public void validate(Node node) throws ValidationFailedException {
    int errorsCount = 0;
    for (int i=0; i<assertions.length; i++) {
      try {
        assertions[i].eval(node);
      } catch (XPathExpressionException e) {
        throw new ValidationFailedException(e.getMessage());
      } catch (AssertionError err) {
        errorsCount++;
        LOG.error(err.getMessage());
      }
    }

    if (errorsCount > 0) {
      throw new ValidationFailedException("Validation failed");
    }
  }
}
