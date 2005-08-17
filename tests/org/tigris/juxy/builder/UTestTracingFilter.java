package org.tigris.juxy.builder;

import junit.framework.TestCase;
import org.tigris.juxy.Tracer;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.SAXSerializer;
import org.tigris.juxy.util.SAXUtil;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.util.XMLComparator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * $Id: UTestTracingFilter.java,v 1.2 2005-08-17 18:21:30 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTracingFilter extends TestCase {
    private static final String JUXY_XMLNS = "xmlns:juxy='" + JuxyParams.NS + "'";
    private static final String TRACER_XMLNS = "xmlns:tracer='java:" + Tracer.class.getName() + "'";
    private static final String XSLT_XMLNS = "xmlns:xsl='" + XSLTKeys.XSLT_NS + "'";
    private static final String JUXY_TRACER_PARAM_TAG = "<xsl:param name='juxy:tracer'/>";
    private static final String AUGMENTED_STYLESHEET_TAG = "<xsl:stylesheet version='1.0' " + JUXY_XMLNS + " " + TRACER_XMLNS + " " + XSLT_XMLNS + ">" + JUXY_TRACER_PARAM_TAG;
    private static final String START_STYLESHEET_TAG = "<xsl:stylesheet version='1.0' " + XSLT_XMLNS + ">\n" + JUXY_TRACER_PARAM_TAG;
    private static final String END_STYLESHEET_TAG = "</xsl:stylesheet>";

    private String filteredStylesheet;

    protected void tearDown() throws Exception {
        // to check that filtered stylesheet is valid
        TransformerFactory.newInstance().
                newTransformer(new StreamSource(new ByteArrayInputStream(filteredStylesheet.getBytes())));
    }

    public void testInstructionOutOfTemplate() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "<xsl:variable name='var1'/>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "<xsl:variable name='var1'/>" +
                END_STYLESHEET_TAG);
    }

    public void testTemplate() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'></xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    public void testValueOf() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:value-of select=\"''\"/>\n" +
                "   </xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:value-of select=\"''\">", 3, 2) +
                "       <xsl:value-of select=\"''\"/>" +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    public void testAttribute() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:element name='root'>\n" +
                "           <xsl:attribute name='id'>\n" +
                "               <xsl:value-of select='5'/>\n" +
                "           </xsl:attribute>\n" +
                "       </xsl:element>\n" +
                "   </xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:element name=\"root\">", 3, 2) +
                "       <xsl:element name='root'>" +
                            makeValueOf("<xsl:attribute name=\"id\">", 4, 3) +
                "           <xsl:attribute name='id'>" +
                                makeValueOf("<xsl:value-of select=\"5\">", 5, 4) +
                "               <xsl:value-of select=\"5\"/>" +
                "           </xsl:attribute>" +
                "       </xsl:element>" +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    public void testIf() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:if test='true()'>\n" +
                "           <root/>\n" +
                "       </xsl:if>\n" +
                "   </xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:if test=\"true()\">", 3, 2) +
                "       <xsl:if test='true()'>\n" +
                            makeValueOf("<root>", 4, 3) +
                "           <root/>\n" +
                "       </xsl:if>\n" +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    public void testChoose() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:choose>\n" +
                "           <xsl:when test='true()'/>\n" +
                "           <xsl:otherwise/>\n" +
                "       </xsl:choose>\n" +
                "   </xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:choose>", 3, 2) +
                "       <xsl:choose>" +
                "           <xsl:when test='true()'>" +
                            makeValueOf("<xsl:when test=\"true()\">", 4, 3) +
                            "</xsl:when>" +
                "           <xsl:otherwise>" +
                            makeValueOf("<xsl:otherwise>", 5, 3) +
                "           </xsl:otherwise>" +
                "       </xsl:choose>" +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    public void testForEach() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:for-each select='/root'>\n" +
                "           <item/>\n" +
                "       </xsl:for-each>\n" +
                "   </xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                "       <xsl:for-each select='/root'>" +
                            makeValueOf("<xsl:for-each select=\"/root\">", 3, 2) +
                            makeValueOf("<item>", 4, 3) +
                "           <item/>" +
                "       </xsl:for-each>" +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    public void testCallTemplate() throws Exception {
        String originalStylesheet = "" +
                START_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:call-template name='tpl2'>\n" +
                "           <xsl:with-param name='p1' select='v1'/>\n" +
                "       </xsl:call-template>\n" +
                "   </xsl:template>\n" +
                "   <xsl:template name='tpl2'>\n" +
                "       <xsl:param name='p1'/>\n" +
                "   </xsl:template>\n" +
                END_STYLESHEET_TAG;
        filter(originalStylesheet);
        assertFilteredEquals("" +
                AUGMENTED_STYLESHEET_TAG +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:call-template name=\"tpl2\">", 3, 2) +
                "       <xsl:call-template name='tpl2'>" +
                "           <xsl:with-param name='p1' select='v1'/>" +
                "       </xsl:call-template>" +
                "   </xsl:template>" +
                "   <xsl:template name='tpl2'>" +
                "       <xsl:param name='p1'/>" +
                    makeValueOf("<xsl:template name=\"tpl2\">", 7, 1) +
                "   </xsl:template>" +
                END_STYLESHEET_TAG);
    }

    private void assertFilteredEquals(String expectedStylesheet) throws SAXException {
        XMLComparator.assertEquals(expectedStylesheet, filteredStylesheet);
    }

    private String makeValueOf(String statement, int line, int col) {
        String escapedStatement = StringUtil.escapeQuoteCharacter(
                StringUtil.escapeXMLText(StringUtil.replaceCharByEntityRef(statement, '\'')));
        return "<xsl:value-of select=\"tracer:trace($juxy:tracer, " + line + ", " + col + ", '" + getSystemId() + "', '" + escapedStatement + "')\"/>";
    }

    private void filter(String originalStylesheet) throws Exception {
        XMLReader reader = SAXUtil.newXMLReader();
        InputSource src = new InputSource();
        src.setSystemId("stylesheet.xsl");
        src.setByteStream(new ByteArrayInputStream(originalStylesheet.getBytes()));
        SAXSerializer s = new SAXSerializer();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(50);
        s.setOutputStream(bos);

        TracingFilter filter = new TracingFilter();
        filter.setParent(reader);
        s.setParent(filter);

        s.parse(src);

        filteredStylesheet = bos.toString();
    }

    private String getSystemId() {
        return new StreamSource(new File("stylesheet.xsl")).getSystemId();
    }
}
