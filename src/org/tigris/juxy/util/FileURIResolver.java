package org.tigris.juxy.util;

import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * $Id: FileURIResolver.java,v 1.4 2006-09-27 17:13:50 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class FileURIResolver implements URIResolver {
    private static final Log logger = LogFactory.getLog(FileURIResolver.class);

    public Source resolve(String href, String base) throws TransformerException {
        logger.debug("Resolving URI: " + href + " against base URI: " + base);
        if (href == null) return null;

        URI hrefURI = null;
        try {
            hrefURI = new URI(href);
        } catch (URISyntaxException e) {
            hrefURI = new File(href).toURI();
        }

        URI baseURI = null;
        if (base != null && base.length() > 0) {
            try {
                baseURI = new URI(base);
            } catch (URISyntaxException e) {
                baseURI = new File(base).getAbsoluteFile().toURI();
            }
        }

        if (baseURI != null)
            hrefURI = baseURI.resolve(hrefURI);

        File file;
        if (hrefURI.isAbsolute() && hrefURI.getScheme().startsWith("file"))
            file = new File(hrefURI);
        else
            file = new File(href).getAbsoluteFile();

        if (file != null && file.exists()) {
            String systemId = file.toURI().toString();
            logger.debug("Resolved URI: " + systemId);
            return new StreamSource(file.toURI().toString());
        }

        logger.debug("Failed to resolve URI");
        return null;
    }
}
