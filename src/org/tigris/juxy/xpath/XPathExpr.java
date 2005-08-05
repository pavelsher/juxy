package org.tigris.juxy.xpath;

import org.jaxen.dom.DOMXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPathSyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import java.util.Collection;

import org.tigris.juxy.util.ArgumentAssert;

/**
 * $Id: XPathExpr.java,v 1.3 2005-08-05 08:31:11 pavelsher Exp $
 * 
 * Simple XPath expressions evaluator. You can evaluate XPath expression to string, int, double, boolean, nodeset or node.
 *
 * @author Pavel Sher
 */
public class XPathExpr
{
    private static final Log logger = LogFactory.getLog(XPathExpr.class);
    private final DOMXPath xpath;

    /**
     * Constructs new XPath expression. Can throw XPathExpressionException exception if an expression syntax incorrect
     * or other errors occured.
     * @param expression an XPath expression
     * @throws XPathExpressionException thrown if an expression syntax is incorrect
     * or other errors occured.
     */
    public XPathExpr(final String expression) throws XPathExpressionException
    {
        ArgumentAssert.notEmpty(expression, "Expression must not be empty");

        try
        {
            xpath = new DOMXPath(expression);
        }
        catch (XPathSyntaxException e)
        {
            logger.error("Error in specified expression syntax: " + e.getMultilineMessage());
            throw new XPathExpressionException(e);
        }
        catch (JaxenException e)
        {
            logger.error("Error in specified expression occured");
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Registers new namespace in XPath expression context. The function returns the same XPathExpr object.
     * @param prefix namespace prefix
     * @param uri namespace uri
     * @return XPathExpr object
     * @throws XPathExpressionException thrown if an error occured
     */
    public XPathExpr addNamespace(String prefix, String uri) throws XPathExpressionException
    {
        ArgumentAssert.notNull(prefix, "Prefix must not be null");
        ArgumentAssert.notEmpty(uri, "URI must not be empty");

        try
        {
            xpath.addNamespace(prefix, uri);
        }
        catch (JaxenException e)
        {
            logger.error("Error occurs during namespace registraion");
            throw new XPathExpressionException(e);
        }

        return this;
    }

    /**
     * Evaluates expression and converts result to boolean.
     * @param node the node on which evaluation is performed.
     * @return boolean result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    public boolean toBoolean(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return xpath.booleanValueOf(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Evaluates expression and converts result to string.
     * @param node the node on which evaluation is performed
     * @return string result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    public String toString(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return xpath.stringValueOf(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Evaluates expression and converts result to int.
     * @param node the node on which evaluation is performed
     * @return int result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    public int toInt(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            Number num = xpath.numberValueOf(node);
            return num.intValue();
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Evaluates expression and converts result to double.
     * @param node the node on which evaluation is performed
     * @return double result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    public double toDouble(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            Number num = xpath.numberValueOf(node);
            return num.doubleValue();
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Evaluates expression and conveerts result to a set of nodes.
     * @param node the node on which evaluation is performed
     * @return a collection of nodes
     * @throws XPathExpressionException thrown if error occurs during XPath evaluation
     */
    public Collection toNodeSet(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return xpath.selectNodes(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Evaluates expression and returns node as its result.
     * @param node the node on which evaluation is performed
     * @return a node
     * @throws XPathExpressionException thrown if error occurs during XPath evaluation
     */
    public Node toNode(Node node) throws XPathExpressionException
    {
        ArgumentAssert.notNull(node, "Node must not be null");
        try
        {
            return (Node) xpath.selectSingleNode(node);
        }
        catch (JaxenException e)
        {
            throw new XPathExpressionException(e);
        }
    }

    /**
     * Returns normalized XPath expression as string. An expression might not be equal to the initial
     * expression specified in the constructor.
     * @return XPath expression as string
     */
    public String getExpression()
    {
        return xpath.toString();
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof XPathExpr)) return false;

        final XPathExpr xPathExpr = (XPathExpr) o;

        if (xpath != null ? !xpath.toString().equals(xPathExpr.xpath.toString()) : xPathExpr.xpath != null) return false;

        return true;
    }

    public int hashCode()
    {
        return (xpath != null ? xpath.toString().hashCode() : 0);
    }
}
