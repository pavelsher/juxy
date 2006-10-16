package org.tigris.juxy.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * User: pavel
 * Date: 10.10.2006
 */
public class LoggingErrorHandler implements ErrorListener, ErrorHandler {
  private final static Log logger = LogFactory.getLog(LoggingErrorHandler.class);
  private int errorsCount = 0;

  public void error(TransformerException exception) throws TransformerException {
    logger.error(ExceptionUtil.exceptionToString(exception, true));
    errorsCount++;
  }

  public void fatalError(TransformerException exception) throws TransformerException {
    logger.fatal(ExceptionUtil.exceptionToString(exception, true));
    errorsCount++;
  }

  public void warning(TransformerException exception) throws TransformerException {
    logger.warn(ExceptionUtil.exceptionToString(exception, true));
  }

  public void warning(SAXParseException exception) throws SAXException {
    logger.warn(ExceptionUtil.exceptionToString(exception, true));
  }

  public void error(SAXParseException exception) throws SAXException {
    logger.error(ExceptionUtil.exceptionToString(exception, true));
    errorsCount++;
  }

  public void fatalError(SAXParseException exception) throws SAXException {
    logger.fatal(ExceptionUtil.exceptionToString(exception, true));
    errorsCount++;
  }

  public void reset() {
    errorsCount = 0;
  }

  public int getErrorsCount() {
    return errorsCount;
  }
  
  public boolean wereErrors() {
    return errorsCount > 0;
  }
}
