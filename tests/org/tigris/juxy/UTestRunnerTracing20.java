package org.tigris.juxy;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

/**
 * Sanity tests for stylesheet correctness after the tracing augmentation
 */
public class UTestRunnerTracing20 extends JuxyTestCase {
    private static final String START_STYLESHEET_TAG = "<xsl:stylesheet version='2.0' xmlns:xsl='" + XSLTKeys.XSLT_NS + "'>\n";
    private static final String END_STYLESHEET_TAG = "</xsl:stylesheet>";

    protected void setUp() throws Exception {
        enableTracing();
    }

    public void testAnalyzeString() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<xsl:analyze-string select=\"'string'\" regex='string'>\n" +
                "<xsl:matching-substring>\n" +
                "matched" +
                "</xsl:matching-substring>\n" +
                "<xsl:non-matching-substring>\n" +
                "not matched" +
                "</xsl:non-matching-substring>\n" +
                "</xsl:analyze-string>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testNamespace() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<result>\n" +
                "<xsl:namespace name='x'>x:uri</xsl:namespace>\n" +
                "</result>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testNextMatch() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<xsl:next-match/>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testPerformSort() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<xsl:perform-sort select='//*'>\n" +
                "<xsl:sort select='@name'/>" +
                "</xsl:perform-sort>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testSequence() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<xsl:sequence select='1 to 5'></xsl:sequence>\n" +
                "</xsl:template>");
        context().setDocument("<root/>");
        applyTemplates();
    }

    public void testFallback() throws Exception {
        setStylesheet("" +
                "<xsl:template match='/'>\n" +
                "<xsl:sequence select='1 to 5'><xsl:fallback>failed</xsl:fallback></xsl:sequence>\n" +
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
