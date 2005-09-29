package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * $Id: BuilderErrorListener.java,v 1.3 2005-09-29 06:48:29 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class BuilderErrorListener implements ErrorListener {
    private static Log logger = LogFactory.getLog(BuilderErrorListener.class);

    public void error(TransformerException exception) throws TransformerException {
        logger.error(exception.getMessageAndLocation());
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        logger.fatal(exception.getMessageAndLocation());
    }

    public void warning(TransformerException exception) throws TransformerException {
        logger.warn(exception.getMessageAndLocation());
    }
}
