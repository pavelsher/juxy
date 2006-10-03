package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.util.ExceptionUtil;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * @author Pavel Sher
 */
public class BuilderErrorListener implements ErrorListener {
    private static Log logger = LogFactory.getLog(BuilderErrorListener.class);

    public void error(TransformerException exception) throws TransformerException {
        logger.error(ExceptionUtil.exceptionToString(exception, true));
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        logger.fatal(ExceptionUtil.exceptionToString(exception, true));
    }

    public void warning(TransformerException exception) throws TransformerException {
        logger.warn(ExceptionUtil.exceptionToString(exception, true));
    }
}
