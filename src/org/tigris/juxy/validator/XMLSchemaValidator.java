package org.tigris.juxy.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.JuxyRuntimeException;
import org.tigris.juxy.util.ExceptionUtil;
import org.tigris.juxy.util.ArgumentAssert;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
  private SchemaErrorHandler errorHandler;

  public XMLSchemaValidator(String pathToSchema) {
    ArgumentAssert.notEmpty(pathToSchema, "Path must not be empty");

    if (schemaFactory == null) {
      schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    SchemaErrorHandler schemaErrorHandler = new SchemaErrorHandler();
    schemaFactory.setErrorHandler(schemaErrorHandler);
    try {
      validator = schemaFactory.newSchema(new File(pathToSchema)).newValidator();
      if (schemaErrorHandler.wereErrors()) {
        throw new JuxyRuntimeException(
            exceptionMessage("Failed to compile schema", schemaErrorHandler));
      }

      errorHandler = new SchemaErrorHandler();
      validator.setErrorHandler(errorHandler);
    } catch (SAXException e) {
      throw new JuxyRuntimeException(
          exceptionMessage("Failed to compile schema", schemaErrorHandler));
    }
  }

  public void validate(Node node) throws ValidationFailedException {
    ArgumentAssert.notNull(node, "Node must not be null");
    errorHandler.reset();

    Source src = new DOMSource(node);
    try {
      validator.validate(src);
      if (errorHandler.wereErrors()) {
        throw new ValidationFailedException(exceptionMessage("Validation failed", errorHandler));
      }
    } catch (SAXException e) {
      throw new ValidationFailedException(exceptionMessage("Validation failed", errorHandler));
    } catch (IOException e) {
      throw new ValidationFailedException(exceptionMessage("Validation failed", errorHandler));
    }
  }

  private String exceptionMessage(String prefix, SchemaErrorHandler errorHandler) {
    return prefix + (errorHandler.wereErrors() ? ":\n" + errorHandler.getErrorMessages() : "");
  }

  private static class SchemaErrorHandler implements ErrorHandler {
    private final static Log LOG = LogFactory.getLog(XMLSchemaValidator.class);
    private StringBuffer errorMessages;

    public SchemaErrorHandler() {
      errorMessages = new StringBuffer();
    }

    public void warning(SAXParseException exception) throws SAXException {
      LOG.warn(ExceptionUtil.exceptionToString(exception, true));
    }

    public void error(SAXParseException exception) throws SAXException {
      String message = ExceptionUtil.exceptionToString(exception, true);
      LOG.error(message);
      errorMessages.append(message).append("\n");
    }

    public void fatalError(SAXParseException exception) throws SAXException {
      String message = ExceptionUtil.exceptionToString(exception, true);
      LOG.fatal(message);
      errorMessages.append(message).append("\n");
    }

    public boolean wereErrors() {
      return errorMessages.length() > 0;
    }

    public String getErrorMessages() {
      return errorMessages.toString();
    }

    public void reset() {
      errorMessages = new StringBuffer();
    }
  }
}
