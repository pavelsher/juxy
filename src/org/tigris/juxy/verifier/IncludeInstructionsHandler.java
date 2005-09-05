package org.tigris.juxy.verifier;

import org.tigris.juxy.XSLTKeys;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class IncludeInstructionsHandler extends DefaultHandler {
    private Set hrefs = new HashSet();

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (isStopElement(uri, localName))
            throw new ParseStoppedException(null);

        if (isIncludeInstruction(uri, localName)) {
            String href = attributes.getValue("href");
            if (href != null)
                hrefs.add(href);
        }
    }

    public Set getHrefs() {
        return hrefs;
    }

    public void reset() {
        hrefs.clear();
    }

    private boolean isIncludeInstruction(String uri, String localName) {
        return XSLTKeys.XSLT_NS.equals(uri) && INCLUDE_INSTRUCTIONS.contains(localName);
    }

    private boolean isStopElement(String uri, String localName) {
        return XSLTKeys.XSLT_NS.equals(uri) && STOP_ELEMENTS.contains(localName);
    }


    private static Set INCLUDE_INSTRUCTIONS = new HashSet();
    static {
        INCLUDE_INSTRUCTIONS.add("include");
        INCLUDE_INSTRUCTIONS.add("import");
    }

    private static Set STOP_ELEMENTS = new HashSet();
    static {
        STOP_ELEMENTS.add("template");
    }
}
