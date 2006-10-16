package org.tigris.juxy.validator;

import org.tigris.juxy.JuxyRuntimeException;
import org.tigris.juxy.util.LoggingErrorHandler;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

/**
 * User: pavel
 * Date: 10.10.2006
 */
public class XMLSchemaValidator implements Validator {
  private static SchemaFactory schemaFactory = null;
  private javax.xml.validation.Validator validator;
  private LoggingErrorHandler errorHandler;

  public XMLSchemaValidator(String pathToSchema) {
    if (schemaFactory == null) {
      schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    schemaFactory.setErrorHandler(new LoggingErrorHandler());
    try {
      validator = schemaFactory.newSchema(new File(pathToSchema)).newValidator();

      errorHandler = new LoggingErrorHandler();
      validator.setErrorHandler(errorHandler);
    } catch (SAXException e) {
      throw new JuxyRuntimeException("Failed to compile schema", e);
    }
  }

  public void validate(Node node) throws ValidationFailedException {
    errorHandler.reset();

    Source src = new DOMSource(node);
    try {
      validator.validate(src);
      if (errorHandler.wereErrors()) {
        throw new ValidationFailedException("Validation failed");
      }
    } catch (SAXException e) {
      throw new ValidationFailedException("Validation failed: " + e.getMessage());
    } catch (IOException e) {
      throw new ValidationFailedException("Validation failed: " + e.getMessage());
    }
  }
}
