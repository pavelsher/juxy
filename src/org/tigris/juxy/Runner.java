package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;

/**
 * Interface represents Runner. The Runner instance can be obtained from RunnerFactory.
 * <br>
 * An example of Runner usage:<br>
 * <pre>
 * RunnerContext ctx = Runner.newRunnerContext("mystylesheet.xsl");
 * ctx.setDocument("&lt;source/>");
 * Node result = runner.callTemplate(ctx, "templatename");
 * </pre>
 * From this point you can check the result document.
 *
 * @version $Revision: 1.3 $
 * @author Pavel Sher
 */
public interface Runner
{
    /**
     * Creates new RunnerContext object. RunnerContext used for setup of invoked template context.
     * @param stylesheetPath - path to the stylesheet
     * @return new RunnerContext object
     * @throws FileNotFoundException - in case where stylesheetFile does not exists
     */
    RunnerContext newRunnerContext(String stylesheetPath) throws FileNotFoundException;

    /**
     * Calls specified template by its name. This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:call-template name="name">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:call-template>
     * </pre>
     * @param ctx the context in which called template should work
     * @param name the name of called template
     * @return result Node of type DOCUMENT_FRAGMENT, containing the results of the transformation
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
     * @return result Node of type DOCUMENT_FRAGMENT, containing the results of the transformation
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
     * @param selectXpathExpr the XPath expression used in select attribute
     * @return result Node of type DOCUMENT_FRAGMENT, containing the results of the transformation
     */
    Node applyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr) throws TransformerException;

    /**
     * Applies templates matching specified xpath expression and mode starting from specified context.
     * This is equivalent to xslt construction:
     * <pre>
     * &lt;xsl:apply-templates select="xpathexpression" mode="mode">
     *  &lt;xsl:with-param name="param" select="value"/>
     * &lt;/xsl:apply-templates>
     * </pre>
     * @param ctx the context in which applied template should work
     * @param selectXpathExpr the value of select attribute
     * @param mode the value of mode attribute
     * @return result Node of type DOCUMENT_FRAGMENT, containing the results of the transformation
     */
    Node applyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr, String mode) throws TransformerException;
}
