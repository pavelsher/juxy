package org.tigris.juxy.xpath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.JuxyRuntimeException;
import org.tigris.juxy.util.ArgumentAssert;

/**
 * Factory for XPath expressions.
 * @author Pavel Sher
 */
public class XPathFactory {
    private final static Log logger = LogFactory.getLog(XPathFactory.class);
    private static Boolean JAVAX_XPATH_NOT_AVAILABLE = null;
    private static Boolean JAXEN_XPATH_NOT_AVAILABLE = null;

    /**
     * Constructs new XPath expression.
     * @param expression an XPath expression
     * @return newly created XPathExpr object
     */
    public static XPathExpr newXPath(String expression) {
      ArgumentAssert.notEmpty(expression, "XPath expression must not be empty");

      XPathExpr xpath = createJavaxXPath(expression);
      if (xpath != null) return xpath;

      xpath = createJaxenXPath(expression);
      if (xpath != null) return xpath;

      throw new JuxyRuntimeException("Unable to locate XPath implementation");
    }

    protected static XPathExpr createJavaxXPath(String expression) {
      if (Boolean.TRUE.equals(JAVAX_XPATH_NOT_AVAILABLE)) return null;

      try {
        Class.forName("javax.xml.xpath.XPathFactory");
        return new JavaxXPathExpr(expression);
      }
      catch (Throwable t) {
        logger.debug("Failed to obtain instance of the javax.xml.xpath.XPathFactory");
        JAVAX_XPATH_NOT_AVAILABLE = Boolean.TRUE;
      }

      return null;
    }

    protected static XPathExpr createJaxenXPath(String expression) {
      if (Boolean.TRUE.equals(JAXEN_XPATH_NOT_AVAILABLE)) return null;

      try {
        Class.forName("org.jaxen.dom.DOMXPath");
        return new JaxenXPathExpr(expression);
      }
      catch (Throwable t) {
        logger.debug("Failed to obtain instance of the org.jaxen.dom.DOMXPath");
        JAXEN_XPATH_NOT_AVAILABLE = Boolean.TRUE;
      }

      return null;
    }

    protected static void reset() {
      JAVAX_XPATH_NOT_AVAILABLE = null;
      JAXEN_XPATH_NOT_AVAILABLE = null;
    }
}