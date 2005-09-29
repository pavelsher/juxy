package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.util.ExceptionUtil;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * $Id: BuilderErrorListener.java,v 1.4 2005-09-29 07:31:42 pavelsher Exp $
 * <p/>
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
