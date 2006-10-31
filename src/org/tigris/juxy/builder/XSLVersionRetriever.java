package org.tigris.juxy.builder;

import org.tigris.juxy.XSLTKeys;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Pavel Sher
 */
public class XSLVersionRetriever extends DefaultHandler {
  private String version;
  public static final String STOP_MESSAGE = "STOPPED";

  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (XSLTKeys.XSLT_NS.equals(uri) && "stylesheet".equals(localName)) {
      version = attributes.getValue("version");
      throw new SAXException(STOP_MESSAGE);
    }
  }

  public String getVersion() {
    return version;
  }
}
