package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * $Id: BuilderErrorListener.java,v 1.2 2005-08-05 08:38:29 pavelsher Exp $
 * <p/>
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
