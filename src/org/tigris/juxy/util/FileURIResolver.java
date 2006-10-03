package org.tigris.juxy.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
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
