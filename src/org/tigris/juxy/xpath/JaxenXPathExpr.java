package org.tigris.juxy.xpath;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.tigris.juxy.util.ArgumentAssert;
import org.w3c.dom.Node;

import java.util.Collection;

/**
 * $Id: JaxenXPathExpr.java,v 1.1 2005-08-10 08:57:18 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class JaxenXPathExpr implements XPathExpr {
    private String expression;
    private DOMXPath xpath;

    protected JaxenXPathExpr(final String expression)
    {
        assert expression != null;
        this.expression = expression;
    }

    public XPathExpr addNamespace(String prefix, String uri) throws XPathExpressionException
    {
        ArgumentAssert.notNull(prefix, "Prefix must not be null");
        ArgumentAssert.notEmpty(uri, "URI must not be empty");

        try
        {
            xpath().addNamespace(prefix, uri);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Error occured during namespace registraion", e);
        }

        return this;
    }

    private DOMXPath xpath() throws XPathExpressionException {
        if (xpath == null)
            try {
                xpath = new DOMXPath(expression);
            } catch (JaxenException e) {
                throw new XPathExpressionException("Failed to compile XPath expression: " + expression, e);
            }

        return xpath;
    }

    public boolean toBoolean(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return xpath().booleanValueOf(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Failed to evaluate XPath expression", e);
        }
    }

    public String toString(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return xpath().stringValueOf(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Failed to evaluate XPath expression", e);
        }
    }

    public int toInt(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            Number num = xpath().numberValueOf(node);
            return num.intValue();
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Failed to evaluate XPath expression", e);
        }
    }

    public double toDouble(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            Number num = xpath().numberValueOf(node);
            return num.doubleValue();
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Failed to evaluate XPath expression", e);
        }
    }

    public Collection toNodeSet(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return xpath().selectNodes(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Failed to evaluate XPath expression", e);
        }
    }

    public Node toNode(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return (Node) xpath().selectSingleNode(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException("Failed to evaluate XPath expression", e);
        }
    }

    public String getExpression()
    {
        return expression;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JaxenXPathExpr)) return false;

        final JaxenXPathExpr jaxenXPathExpr = (JaxenXPathExpr) o;

        if (!expression.equals(jaxenXPathExpr.expression)) return false;

        return true;
    }

    public int hashCode() {
        return expression.hashCode();
    }
}
