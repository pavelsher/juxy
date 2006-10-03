package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

import java.io.File;

/**
 * Holds all information required for calling / applying templates, i.e:
 * XSLT file, parameters, variables, namespaces and so on.
 *
 * @author Pavel Sher
 */
public interface RunnerContext
{
    /**
     * Sets input document in the context. The string will be parsed in Document object.
     * @param xmlDocument input document
     */
    void setDocument(String xmlDocument);

    /**
     * Sets parsed input document in the context.
     * @param document input document
     */
    void setDocument(Document document);

    /**
     * Sets file as input document in the context.
     * @param file input document xml file
     */
    void setDocument(File file);

    /**
     * Method registers uri and prefix in the context.
     * Method will overwrite already registered prefixes with same uri.
     * @param prefix uri prefix
     * @param uri namespace uri to register
     */
    void registerNamespace(String prefix, String uri);

    /**
     * Removes all previously registered namespaces.
     */
    void clearNamespaces();

    /**
     * Set up current node. Node will be selected by specified XPath expression.
     * @param xpathExpr expression selecting current node.
     */
    void setCurrentNode(XPathExpr xpathExpr);

    /**
     * Set up value of a global parameter.
     * Note: second attempt to set a parameter with the same name will replace previous value with new one.
     * @param qname fully qualified name of the parameter. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param value parameter value
     */
    void setGlobalParamValue(String qname, Object value);

    /**
     * Removes all global parameters.
     */
    void clearGlobalParams();

    /**
     * Set up value of a global variable. Same as the following XSLT construction:
     * <pre>
     * &lt;xsl:variable name="name" select="'value'"/>
     * </pre>
     * Note: all "'" characters will be encoded in XML entity automatically.
     * Note: second attempt to set global variable with the same name will replace previous value with new one.
     * @param qname fully qualified name of the variable. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param value global variable string value
     */
    void setGlobalVariableValue(String qname, String value);

    /**
     * Set up value of a global variable which will be calculated by specified XPath expression.
     * Same as:
     * <pre>
     * &lt;xsl:variable name="name" select="xpathexpression"/>
     * </pre>
     * Note: second attempt to set global variable with the same name will replace previous value with new one.
     * @param qname fully qualified name of the variable. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param xpath the global variable xpath expression
     */
    void setGlobalVariableValue(String qname, XPathExpr xpath);

    /**
     * Set up Document as a value of a global variable. Same as:
     * <pre>
     * &lt;xsl:variable name="name">
     *  &lt;content/>
     * &lt;/xsl:variable>
     * </pre>
     * Note: second attempt to set global variable with the same name will replace previous value with new one.
     * @param qname fully qualified name of the variable. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param variableContent content of a variable
     */
    void setGlobalVariableValue(String qname, Document variableContent);

    /**
     * Removes all global variables.
     */
    void clearGlobalVariables();

    /**
     * Set up string as a value of a template parameter. Same as:
     * <pre>
     * &lt;xsl:with-param name="name" select="'value'"/>
     * </pre>
     * Note: all "'" characters will be encoded in XML entity automatically.
     * Note: second attempt to set template parameter with the same name will replace previous value with new one.
     * @param qname fully qualified name of the parameter. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param value parameter value
     */
    void setTemplateParamValue(String qname, String value);

    /**
     * Set up value of a template parameter which will be calculated by specified XPath expression.
     * Same as:
     * <pre>
     * &lt;xsl:with-param name="name" select="xpathexpression"/>
     * </pre>
     * Note: second attempt to set template parameter with the same name will replace previous value with new one.
     * @param qname fully qualified name of the parameter. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param xpath parameter xpath expression
     */
    void setTemplateParamValue(String qname, XPathExpr xpath);

    /**
     * Set up Document as a value of a template parameter. Same as:
     * <pre>
     * &lt;xsl:with-param name="name">
     *  &lt;content/>
     * &lt;/xsl:with-param>
     * </pre>
     * Note: second attempt to set template parameter with the same name will replace previous value with new one.
     * @param qname fully qualified name of the parameter. If qname contains namespace prefix it (prefix) should be registered
     * using {@link #registerNamespace} method.
     * @param paramContent content of variable
     */
    void setTemplateParamValue(String qname, Document paramContent);

    /**
     * Removes template parameters from context.
     */
    void clearTemplateParams();
}
