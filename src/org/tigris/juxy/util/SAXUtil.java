package org.tigris.juxy.util;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.tigris.juxy.JuxyRuntimeException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * $Id: SAXUtil.java,v 1.2 2005-09-05 17:37:37 pavelsher Exp $
 * <p/>
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
