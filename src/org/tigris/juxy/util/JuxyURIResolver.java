package org.tigris.juxy.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Pavel Sher
 */
public class JuxyURIResolver implements URIResolver {
    private static final Log logger = LogFactory.getLog(JuxyURIResolver.class);

    public Source resolve(String href, String base) {
        logger.debug("Resolving URI: " + href + " against base URI: " + base);
        if (href == null) return null;

        URI hrefURI;
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

        URI resolvedHrefURI = hrefURI;
        if (baseURI != null)
            resolvedHrefURI = resolveFromBase(baseURI, hrefURI);

        if ("jar".equals(resolvedHrefURI.getScheme())) {
            return new StreamSource(resolvedHrefURI.toString());
        }

        File file;
        if (resolvedHrefURI.isAbsolute() && "file".equals(resolvedHrefURI.getScheme()))
            file = new File(resolvedHrefURI);
        else
            file = new File(href).getAbsoluteFile();

        if (file != null && file.exists()) {
            String systemId = file.toURI().toString();
            logger.debug("Resolved URI: " + systemId);
            return new StreamSource(file.toURI().toString());
        }

        // attempt to resolve URI via classloader
        String resourceName = resolvedHrefURI.toString();
        if (resolvedHrefURI.getScheme() != null) {
            resourceName = resolvedHrefURI.getSchemeSpecificPart();
        }

        URL found = getClass().getResource(resourceName);
        if (found != null) {
          try {
            String resolved = found.toURI().toString();
            logger.debug("URI was resolved via classloader resources: " + resolved);
            return new StreamSource(resolved);
          } catch (URISyntaxException e) {
            logger.error(e);
          }
        }

        logger.warn("Failed to resolve URI: " + href);
        return null;
    }

  private String getResourcePath(final String uri) {
    int absPathIdx = uri.indexOf("!");
    if (absPathIdx == -1) return "/";
    if (absPathIdx == uri.length() - 1) {
      return "/";
    }

    return uri.substring(absPathIdx+1);
  }

  private String getResourceBase(final String uri) {
    int absPathIdx = uri.indexOf("!");
    if (absPathIdx == -1 || absPathIdx == uri.length() - 1) return uri;

    return uri.substring(0, absPathIdx);
  }


  private URI resolveFromBase(final URI baseURI, URI hrefURI) {
    if ("jar".equals(baseURI.getScheme())) {
      if ("jar".equals(hrefURI.getScheme())) return hrefURI;
      if (hrefURI.isAbsolute()) return hrefURI;

      String path = getResourcePath(baseURI.toString());
      try {
        hrefURI = new URI(path).resolve(hrefURI);
        return new URI(getResourceBase(baseURI.toString()) + "!" + hrefURI.toString());
      } catch (URISyntaxException e) {
        logger.debug(e.toString());
      }
    }

    return baseURI.resolve(hrefURI);
  }
}
