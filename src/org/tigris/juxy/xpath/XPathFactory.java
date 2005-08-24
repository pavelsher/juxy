package org.tigris.juxy.xpath;

import org.tigris.juxy.JuxyRuntimeException;
import org.tigris.juxy.util.ArgumentAssert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.xpath.XPathFactoryConfigurationException;

/**
 * $Id: XPathFactory.java,v 1.2 2005-08-24 08:28:30 pavelsher Exp $
 * <p/>
 * Factory for XPath expressions.
 * @author Pavel Sher
 */
public class XPathFactory {
    private final static Log logger = LogFactory.getLog(XPathFactory.class);

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
                return new JavaxXPathExpr(expression);
            }
        }
        catch (ClassNotFoundException e) {}
        catch (XPathFactoryConfigurationException e) {
            logger.debug("Failed to obtain instance of the javax.xml.xpath.XPathFactory");
        }

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