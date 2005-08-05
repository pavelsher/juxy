package org.tigris.juxy.builder;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.tigris.juxy.XSLTKeys;

/**
 * $Id: XSLVersionRetriever.java,v 1.2 2005-08-05 08:38:29 pavelsher Exp $
 * <p/>
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
