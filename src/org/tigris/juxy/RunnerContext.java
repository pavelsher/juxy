package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * $Id: RunnerContext.java,v 1.5 2005-08-07 17:29:55 pavelsher Exp $
 * <p/>
 * The class represents Runner Context. Instance of the Context can be obtained from Runner.<br>
 * Runner Context is used to setup all the information which is required to start xsl transformation:
 * xsl file, parameters, variables and so on.
 *
 * @author Pavel Sher
 */
public interface RunnerContext
{
    /**
     * Sets input document in the context. The string will be parsed in Document object.
     * @param document input document
     */
    void setDocument(String document);

    /**
     * Method registers uri and prefix in the context.
     * Method will overwrite already registered prefixes with same uri.
     * @param prefix uri prefix
     * @param uri namespace uri to register
     */
    void registerNamespace(String prefix, String uri);

    /**
     * Clears all previously registered namespaces.
     */
    void clearNamespaces();

    /**
     * Sets xpath expression used for selection of current node.
     * @param xpathExpr an expression used for selection of current node.
     */
    void setCurrentNode(XPathExpr xpathExpr);

    /**
     * Sets global parameter value in context.
     * Note: second setting of param with the same name replaces previous value with new one.
     * @param qname param fully qualified name. If qname contains namespace prefix, you
     * should register parameter namespace with registerNamespace method.
     * @param value param value
     */
    void setGlobalParamValue(String qname, Object value);

    /**
     * Clears global parameters.
     */
    void clearGlobalParams();

    /**
     * Sets string value for global variable. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:variable name="name" select="'value'"/>
     * </pre>
     * Note: all "'" characters will be encoded in XML entity automatically.
     * Note: second setting of global variable with the same name replaces previous value with new one.
     * @param qname global variable fully qualified name. If qname contains namespace prefix, you
     * should register variable namespace with registerNamespace method.
     * @param value global variable string value
     */
    void setGlobalVariableValue(String qname, String value);

    /**
     * Sets xpath expression for global variable. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:variable name="name" select="xpathexpression"/>
     * </pre>
     * Note: second setting of global variable with the same name replaces previous value with new one.
     * @param qname global variable fully qualified name. If qname contains namespace prefix, you
     * should register variable namespace with registerNamespace method.
     * @param xpath the global variable xpath expression
     */
    void setGlobalVariableValue(String qname, XPathExpr xpath);

    /**
     * Sets content for global variable. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:variable name="name">
     *  &lt;content/>
     * &lt;/xsl:variable>
     * </pre>
     * Note: second setting of global variable with the same name replaces previous value with new one.
     * @param qname global variable fully qualified name. If qname contains namespace prefix, you
     * should register variable namespace with registerNamespace method.
     * @param variableContent content of variable
     */
    void setGlobalVariableValue(String qname, Document variableContent);

    /**
     * Clears global variables from context.
     */
    void clearGlobalVariables();

    /**
     * Sets string value for template parameter. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:with-param name="name" select="'value'"/>
     * </pre>
     * Note: all "'" characters will be encoded in XML entity automatically.
     * Note: second setting of template parameter with the same name replaces previous value with new one.
     * @param qname parameter fully qualified name. If qname contains namespace prefix, you
     * should register parameter namespace with registerNamespace method.
     * @param value - the parameter string value
     */
    void setTemplateParamValue(String qname, String value);

    /**
     * Sets xpath expression for template parameter. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:with-param name="name" select="xpathexpression"/>
     * </pre>
     * Note: second setting of template parameter with the same name replaces previous value with new one.
     * @param qname parameter fully qualified name. If qname contains namespace prefix, you
     * should register parameter namespace with registerNamespace method.
     * @param xpath parameter xpath expression
     */
    void setTemplateParamValue(String qname, XPathExpr xpath);

    /**
     * Sets content for template parameter. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:with-param name="name">
     *  &lt;content/>
     * &lt;/xsl:with-param>
     * </pre>
     * Note: second setting of template parameter with the same name replaces previous value with new one.
     * @param qname parameter fully qualified name. If qname contains namespace prefix, you
     * should register parameter namespace with registerNamespace method.
     * @param variableContent content of variable
     */
    void setTemplateParamValue(String qname, Document variableContent);

    /**
     * Clears template parameters from context.
     */
    void clearTemplateParams();
}
