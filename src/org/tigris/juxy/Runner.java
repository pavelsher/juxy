package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

/**
 * Interface represents Runner. Runner instance can be obtained from the {@link RunnerFactory}.
 * <br>
 * An example of Runner usage:<br>
 * <pre>
 * RunnerContext ctx = Runner.newRunnerContext("mystylesheet.xsl");
 * ctx.setDocument("&lt;source/>");
 * Node result = runner.callTemplate(ctx, "templatename");
 * </pre>
 * From this point the received result node can be verified.
 *
 * @author Pavel Sher
 */
public interface Runner
{
    /**
     * Creates a new RunnerContext object. RunnerContext holds all information required
     * for calling / applying templates.
     * @param systemId system id of the stylesheet (path to a stylesheet file)
     * @return new RunnerContext object
     */
    RunnerContext newRunnerContext(String systemId);

    /**
     * Creates a new RunnerContext object. RunnerContext holds all information required
     * for calling / applying templates.
     * @param systemId system id of the stylesheet
     * @param resolver URIResolver to use for URI resolution during transformation
     * @return new RunnerContext object
     */
    RunnerContext newRunnerContext(String systemId, URIResolver resolver);

    /**
     * Invokes specified named template. This is the same as to have the following XSLT construction:
     * <pre>
     * &lt;xsl:call-template name="name">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:call-template>
     * </pre>
     * @param ctx context in which the template will be called
     * @param name name of the template to call
     * @return result result of the transformation
     * @throws javax.xml.transform.TransformerException if transformation failed
     */
    Node callTemplate(RunnerContext ctx, String name) throws TransformerException;

    /**
     * Applies templates in the specified context. Same as:
     * <pre>
     * &lt;xsl:apply-templates>
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx context in which the templates are applied
     * @return result result of the transformation
     * @throws javax.xml.transform.TransformerException if transformation failed
     */
    Node applyTemplates(RunnerContext ctx) throws TransformerException;

    /**
     * Applies templates for specified xpath expression. Same as:
     * <pre>
     * &lt;xsl:apply-templates select="xpathexpression">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx context in which the templates are applied
     * @param xpath XPath expression to use as the select attribute value
     * @return result result of the transformation
     * @throws javax.xml.transform.TransformerException if transformation failed
     */
    Node applyTemplates(RunnerContext ctx, XPathExpr xpath) throws TransformerException;

    /**
     * Applies templates for specified xpath expression and mode.
     * Same as:
     * <pre>
     * &lt;xsl:apply-templates select="xpathexpression" mode="mode">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx context in which the templates are applied
     * @param xpath XPath expression to use as the select attribute value
     * @param mode mode attribute value
     * @return result results of the transformation
     * @throws javax.xml.transform.TransformerException if transformation failed
     */
    Node applyTemplates(RunnerContext ctx, XPathExpr xpath, String mode) throws TransformerException;

    /**
     * Enables tracing of XSLT instructions (disabled by default).
     * Tracing output will be passed to stdout.
     */
    void enableTracing();

    /**
     * Disables tracing of XSLT instructions.
     */
    void disableTracing();
}
