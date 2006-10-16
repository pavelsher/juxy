package org.tigris.juxy.validator;

import org.tigris.juxy.JuxyRuntimeException;
import org.tigris.juxy.xpath.XPathAssert;

/**
 * User: pavel
 * Date: 10.10.2006
 */
public class ValidatorFactory {
  private static Boolean JAVAX_SCHEMA_VALIDATION_AVAILABLE = null;

  public static Validator createXMLSchemaValidator(String path) {
    if (Boolean.FALSE.equals(JAVAX_SCHEMA_VALIDATION_AVAILABLE)) {
      throw new JuxyRuntimeException("W3C XML Schema validator is not available");
    }

    if (JAVAX_SCHEMA_VALIDATION_AVAILABLE == null) {
      try {
        Class.forName("javax.xml.validation.SchemaFactory");
        JAVAX_SCHEMA_VALIDATION_AVAILABLE = Boolean.TRUE;
      } catch (Throwable t) {
        JAVAX_SCHEMA_VALIDATION_AVAILABLE = Boolean.FALSE;
      }
    }

    if (Boolean.TRUE.equals(JAVAX_SCHEMA_VALIDATION_AVAILABLE)) {
      return new XMLSchemaValidator(path);
    }

    throw new JuxyRuntimeException("W3C XML Schema validator is not available");
  }

  public static Validator createXPathValidator(final XPathAssert[] assertions) {
    return new XPathValidator(assertions);
  }
}
