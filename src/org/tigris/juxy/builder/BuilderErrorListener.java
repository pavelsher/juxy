package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * $Id: BuilderErrorListener.java,v 1.1 2005-07-29 17:43:43 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class BuilderErrorListener implements ErrorListener {
    private static Log logger = LogFactory.getLog(BuilderErrorListener.class);

    public void error(TransformerException exception) throws TransformerException {
        logger.error(exception);
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        logger.fatal(exception);
    }

    public void warning(TransformerException exception) throws TransformerException {
        logger.warn(exception);
    }
}
