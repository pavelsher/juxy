package org.tigris.juxy.validator;

import org.tigris.juxy.JuxyRuntimeException;

/**
 * @author pavel
 */
public class ValidatorFactory {
  private static Boolean JAVAX_SCHEMA_VALIDATION_AVAILABLE = null;

  /**
   * Creates new W3C XML schema validator.
   * @param systemId path in the file system or name of the classloader resource
   * @return
   */
  public static Validator createXMLSchemaValidator(String systemId) {
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
      return new XMLSchemaValidator(systemId);
    }

    throw new JuxyRuntimeException("W3C XML Schema validator is not available");
  }
}
