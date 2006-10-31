package org.tigris.juxy.util;

import org.tigris.juxy.JuxyRuntimeException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author Pavel Sher
 */
public class SAXUtil {
  private static SAXParserFactory parserFactory;

  public static XMLReader newXMLReader() {
    try {
      return getParserFactory().newSAXParser().getXMLReader();
    } catch (ParserConfigurationException e) {
      throw new JuxyRuntimeException("Failed to create SAX parser", e);
    } catch (SAXException e) {
      throw new JuxyRuntimeException("Failed to create SAX parser", e);
    }
  }

  private static SAXParserFactory getParserFactory() {
    if (parserFactory == null) {
      parserFactory = SAXParserFactory.newInstance();
      parserFactory.setNamespaceAware(true);
      parserFactory.setValidating(false);
    }

    return parserFactory;
  }
}
