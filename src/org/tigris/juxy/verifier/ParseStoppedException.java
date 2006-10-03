package org.tigris.juxy.verifier;

import org.xml.sax.SAXException;

/**
 */
public class ParseStoppedException extends SAXException {
    // This constructor is required if Juxy is running under
    // the JDK 1.4. DO NOT REMOVE THIS CONSTRUCTOR!
    public ParseStoppedException(String message) {
        super(message);
    }
}
