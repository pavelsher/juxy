package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

/**
 * $Id: Runner.java,v 1.9 2005-09-23 09:37:01 pavelsher Exp $
 * <p/>
 * Interface represents Runner. Runner instance can be obtained from the RunnerFactory.
 * <br>
 * An example of Runner usage:<br>
 * <pre>
 * RunnerContext ctx = Runner.newRunnerContext("mystylesheet.xsl");
 * ctx.setDocument("&lt;source/>");
 * Node result = runner.callTemplate(ctx, "templatename");
 * </pre>
 * From this point the result can be verified.
 *
 * @author Pavel Sher
 */
public interface Runner
{
    /**
     * Creates new RunnerContext object. RunnerContext is used for setup of the context
     * in which the called template will be running.
     * @param systemId system id of the stylesheet
     * @return new RunnerContext object
     */
    RunnerContext newRunnerContext(String systemId);

    /**
     * Creates new RunnerContext object. RunnerContext is used for setup of the context
     * in which the called template will be running.
     * @param systemId system id of the stylesheet
     * @param resolver URIResolver to use for system id resolution during transformation
     * @return new RunnerContext object
     */
    RunnerContext newRunnerContext(String systemId, URIResolver resolver);

    /**
     * Calls specified template by its name. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:call-template name="name">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:call-template>
     * </pre>
     * @param ctx the context in which called template should work
     * @param name the name of called template
     * @return result Node, containing the results of the transformation
     */
    Node callTemplate(RunnerContext ctx, String name) throws TransformerException;

    /**
     * Applies all templates starting from specified context. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:apply-templates>
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx the context in which applied template should work
     * @return result Node, containing the results of the transformation
     */
    Node applyTemplates(RunnerContext ctx) throws TransformerException;

    /**
     * Applies template matching specified xpath expression starting from specified context.
     * This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:apply-templates select="xpathexpression">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx the context in which applied template should work
     * @param xpath the XPath expression to use as the select attribute value
     * @return result Node, containing the results of the transformation
     */
    Node applyTemplates(RunnerContext ctx, XPathExpr xpath) throws TransformerException;

    /**
     * Applies templates matching specified xpath expression and mode starting from specified context.
     * This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:apply-templates select="xpathexpression" mode="mode">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx the context in which applied template should work
     * @param xpath the XPath expression to use as the select attribute value
     * @param mode the value of mode attribute
     * @return result Node, containing the results of the transformation
     */
    Node applyTemplates(RunnerContext ctx, XPathExpr xpath, String mode) throws TransformerException;

    /**
     * Enables tracing of the XSLT stylesheet execution (disabled by default).
     * Tracing information is dumped on stdout.
     */
    void enableTracing();

    /**
     * Disables tracing of the XSLT stylesheet execution
     */
    void disableTracing();
}
