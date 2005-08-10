package org.tigris.juxy.xpath;

import org.tigris.juxy.JuxyRuntimeException;
import org.tigris.juxy.util.ArgumentAssert;

/**
 * $Id: XPathFactory.java,v 1.1 2005-08-10 08:57:18 pavelsher Exp $
 * <p/>
 * Factory for XPath expressions.
 * @author Pavel Sher
 */
public class XPathFactory {

    /**
     * Constructs new XPath expression.
     * @param expression an XPath expression
     */
    public static XPathExpr newXPath(String expression) {
        ArgumentAssert.notEmpty(expression, "XPath expression must not be empty");
        XPathExpr xpath = createJava50XPath(expression);
        if (xpath == null)
            xpath = createJaxenXPath(expression);

        if (xpath != null) return xpath;
        throw new JuxyRuntimeException("Unable to locate XPath implementation");
    }

    protected static XPathExpr createJava50XPath(String expression) {
        try {
            if (Class.forName("javax.xml.xpath.XPathFactory") != null) {
                return new Java50XPathExpr(expression);
            }
        } catch (ClassNotFoundException e) {}
        return null;
    }

    protected static XPathExpr createJaxenXPath(String expression) {
        try {
            if (Class.forName("org.jaxen.dom.DOMXPath") != null) {
                return new JaxenXPathExpr(expression);
            }
        } catch (ClassNotFoundException e) {}
        return null;
    }
}