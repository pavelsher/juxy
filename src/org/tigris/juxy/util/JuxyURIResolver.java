package org.tigris.juxy.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        try {
          baseURI = new File(base).getCanonicalFile().toURI();
        } catch (IOException e1) {
          logger.error(e1);
        }
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
      file = new File(href);

    try {
      file = file.getCanonicalFile();
    } catch (IOException e) {
      logger.error(e);
    }

    if (file != null && file.exists()) {
      String systemId = file.toURI().toString();
      logger.debug("Resolved URI: " + systemId);
      return new StreamSource(systemId);
    }

    // attempt to resolve URI via classloader
    String resourceName = resolvedHrefURI.toString();
    if (resolvedHrefURI.getScheme() != null) {
      resourceName = resolvedHrefURI.getSchemeSpecificPart();
    }

    URL found = getClass().getResource(resourceName);
    if (found != null) {
      String resolved = found.toString();
      logger.debug("URI was resolved via classloader resources: " + resolved);
      return new StreamSource(resolved);
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

    return uri.substring(absPathIdx + 1);
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

  private String toSystemId(File f) {
    String fpath=f.getAbsolutePath();
    if (File.separatorChar != '/') {
        fpath = fpath.replace(File.separatorChar, '/');
    }
    if( fpath.startsWith("/"))
      return "file://" + fpath;
    else
      return "file:///" + fpath;
  }
}
