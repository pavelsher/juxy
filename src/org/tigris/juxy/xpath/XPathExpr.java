package org.tigris.juxy.xpath;

import org.w3c.dom.Node;

import java.util.List;

/**
 * $Id: XPathExpr.java,v 1.7 2005-09-12 07:43:48 pavelsher Exp $
 * <p/>
 * Simple XPath expressions evaluator. You can evaluate XPath expression to string, int, double, boolean, nodeset or node.
 *
 * @author Pavel Sher
 */
public interface XPathExpr {
    /**
     * Registers new namespace in the XPath expression context. The function returns the same XPathExpr object.
     * @param prefix namespace prefix
     * @param uri namespace uri
     * @return XPathExpr object
     * @throws XPathExpressionException thrown if an error occured
     */
    XPathExpr addNamespace(String prefix, String uri) throws XPathExpressionException;

    /**
     * Evaluates expression and converts result to boolean.
     * @param node the node on which evaluation is performed.
     * @return boolean result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    boolean toBoolean(Node node) throws XPathExpressionException;

    /**
     * Evaluates expression and converts result to string.
     * @param node the node on which evaluation is performed
     * @return string result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    String toString(Node node) throws XPathExpressionException;

    /**
     * Evaluates expression and converts result to int.
     * @param node the node on which evaluation is performed
     * @return int result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    int toInt(Node node) throws XPathExpressionException;

    /**
     * Evaluates expression and converts result to double.
     * @param node the node on which evaluation is performed
     * @return double result of XPath expression
     * @throws XPathExpressionException thrown if error occured during XPath evaluation
     */
    double toDouble(Node node) throws XPathExpressionException;

    /**
     * Evaluates expression and conveerts result to a set of nodes.
     * @param node the node on which evaluation is performed
     * @return a collection of nodes
     * @throws XPathExpressionException thrown if error occurs during XPath evaluation
     */
    List toNodeList(Node node) throws XPathExpressionException;

    /**
     * Evaluates expression and returns node as its result.
     * @param node the node on which evaluation is performed
     * @return a node
     * @throws XPathExpressionException thrown if error occurs during XPath evaluation
     */
    Node toNode(Node node) throws XPathExpressionException;

    /**
     * Returns this XPath expression as a String
     * @return expression
     */
    String getExpression();
}
