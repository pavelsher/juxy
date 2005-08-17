package org.tigris.juxy;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

/**
 * Sanity tests for stylesheet correctness after the tracing augmentation
 */
public class UTestRunnerTracing extends JuxyTestCase {
    protected void setUp() throws Exception {
        enableTracing();
    }

    public void testNoTemplatesNoTracing() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:variable name='var1'/>\n" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testNamedTemplate() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template name='tpl'/>\n" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        callTemplate("tpl");
    }

    public void testTemplateWithMode() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/' mode='start'/>\n" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates(xpath("/"), "start");
    }

    public void testIf_NotEntered() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/'>\n" +
                "   <xsl:if test='false()'>bbb</xsl:if>\n" +
                "</xsl:template>\n" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testIf_Entered() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/'>\n" +
                "   <xsl:if test='true()'>bbb</xsl:if>\n" +
                "</xsl:template>\n" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testIf_InAttribute() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/'>\n" +
                "<xsl:element name='elem'>" +
                "   <xsl:attribute name='zzz'>" +
                "   <xsl:if test='true()'>bbb</xsl:if>" +
                "   </xsl:attribute>" +
                "</xsl:element>" +
                "</xsl:template>" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testChoose() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/'>\n" +
                "   <xsl:choose>" +
                "   <xsl:when test='false()'/>" +
                "   <xsl:otherwise><root/></xsl:otherwise>" +
                "   </xsl:choose>" +
                "</xsl:template>" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testCallTemplate() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/'>\n" +
                "   <xsl:call-template name='tpl'>\n" +
                "       <xsl:with-param name='p1' select='v1'/>" +
                "   </xsl:call-template>" +
                "</xsl:template>" +
                "<xsl:template name='tpl'>" +
                "   <xsl:param name='p1'/>" +
                "</xsl:template>" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testApplyTemplates() throws Exception {
        setStylesheet("" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:template match='/'>\n" +
                "   <xsl:apply-templates/>" +
                "</xsl:template>" +
                "</xsl:stylesheet>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    private void setStylesheet(final String stylesheet) {
        newContext("stylesheet.xsl", new URIResolver() {
            public Source resolve(String href, String base) {
                StreamSource src = new StreamSource();
                src.setSystemId("stylesheet.xsl");
                src.setInputStream(new ByteArrayInputStream(stylesheet.getBytes()));
                return src;
            }
        });
    }
}
