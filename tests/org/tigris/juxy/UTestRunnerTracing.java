package org.tigris.juxy;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

/**
 * Sanity tests for stylesheet correctness after the tracing augmentation
 */
public class UTestRunnerTracing extends JuxyTestCase {
    private static final String START_STYLESHEET_TAG = "<xsl:stylesheet version='1.0' xmlns:xsl='" + XSLTKeys.XSLT_NS + "'>\n";
    private static final String END_STYLESHEET_TAG = "</xsl:stylesheet>";

    protected void setUp() throws Exception {
        enableTracing();
    }

    public void testNoTemplatesNoTracing() throws Exception {
        setStylesheet("<xsl:variable name='var1'/>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testNamedTemplate() throws Exception {
        setStylesheet("<xsl:template name='tpl'/>");
        context().setDocument("<root/>");
        callTemplate("tpl");
    }

    public void testTemplateWithMode() throws Exception {
        setStylesheet("<xsl:template match='/' mode='start'/>");
        context().setDocument("<root/>");
        applyTemplates(xpath("/"), "start");
    }

    public void testIf_NotEntered() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:if test='false()'>bbb</xsl:if>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testIf_Entered() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:if test='true()'>bbb</xsl:if>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testIf_InAttribute() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<xsl:element name='elem'>" +
                "   <xsl:attribute name='zzz'>" +
                "   <xsl:if test='true()'>bbb</xsl:if>" +
                "   </xsl:attribute>" +
                "</xsl:element>" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testChoose() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:choose>" +
                "   <xsl:when test='false()'/>" +
                "   <xsl:otherwise><root/></xsl:otherwise>" +
                "   </xsl:choose>" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testCallTemplate() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:call-template name='tpl'>\n" +
                "       <xsl:with-param name='p1' select='v1'/>" +
                "   </xsl:call-template>" +
                "</xsl:template>" +
                "<xsl:template name='tpl'>" +
                "   <xsl:param name='p1'/>" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testApplyTemplates() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:apply-templates/>" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testApplyImports() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:apply-imports/>" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testForEach() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:for-each select='//*'>\n" +
                "       <xsl:value-of select='.'/>\n" +
                "   </xsl:for-each>" +
                "</xsl:template>");
        context().setDocument("<root><item/></root>");
        applyTemplates();
    }

    public void testVariable() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:variable name='v' select='*'/>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testTextNodesExist() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   first text string\n" +
                "   second text string\n" +
                "   <p>a paragraph content</p>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testXslText() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   first text string\n" +
                "   <xsl:text>second text string</xsl:text>\n" +
                "   <p>a paragraph content</p>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testProcessingInstructionsAndText() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <xsl:text>\n" +
                "   <?first?>\n" +
                "   <?second param='value'?>\n" +
                "   <?third param=\"value\"?>\n" +
                "   some text\n" +
                "   </xsl:text>\n" +
                "   text out of xsl:text\n" +
                "   <p>a paragraph content</p>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testPrefixesConflict() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "   <tracer:trace xmlns:tracer='tracer.uri'/>\n" +
                "   <juxy:trace xmlns:juxy='juxy.uri'/>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    private void setStylesheet(final String stylesheet) {
        newContext("stylesheet.xsl", new URIResolver() {
            public Source resolve(String href, String base) {
                StreamSource src = new StreamSource();
                src.setSystemId("stylesheet.xsl");
                String actualStylesheet = START_STYLESHEET_TAG + stylesheet + END_STYLESHEET_TAG;
                src.setInputStream(new ByteArrayInputStream(actualStylesheet.getBytes()));
                return src;
            }
        });
    }
}
