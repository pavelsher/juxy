package org.tigris.juxy.builder;

import junit.framework.TestCase;
import org.tigris.juxy.util.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * $Id: UTestTracingFilter.java,v 1.1 2005-08-17 17:54:52 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTracingFilter extends TestCase {
    private TransformerFactory transformerFactory;

    protected void setUp() throws Exception {
        transformerFactory = TransformerFactory.newInstance();
    }

    public void testInstructionOutOfTemplate() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<xsl:variable name='var1'/>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "<xsl:variable name='var1'/>" +
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testTemplate() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'></xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                "   </xsl:template>" +
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testValueOf() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:value-of select=\"''\"/>\n" +
                "   </xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:value-of select=\"''\">", 3, 2) +
                "       <xsl:value-of select=\"''\"/>" +
                "   </xsl:template>" +
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testAttribute() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:element name='root'>\n" +
                "           <xsl:attribute name='id'>\n" +
                "               <xsl:value-of select='5'/>\n" +
                "           </xsl:attribute>\n" +
                "       </xsl:element>\n" +
                "   </xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
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
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testIf() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:if test='true()'>\n" +
                "           <root/>\n" +
                "       </xsl:if>\n" +
                "   </xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                        makeValueOf("<xsl:if test=\"true()\">", 3, 2) +
                "       <xsl:if test='true()'>\n" +
                            makeValueOf("<root>", 4, 3) +
                "           <root/>\n" +
                "       </xsl:if>\n" +
                "   </xsl:template>" +
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testChoose() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:choose>\n" +
                "           <xsl:when test='true()'/>\n" +
                "           <xsl:otherwise/>\n" +
                "       </xsl:choose>\n" +
                "   </xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
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
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testForEach() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:for-each select='/root'>\n" +
                "           <item/>\n" +
                "       </xsl:for-each>\n" +
                "   </xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>" +
                        makeValueOf("<xsl:template name=\"tpl\">", 2, 1) +
                "       <xsl:for-each select='/root'>" +
                            makeValueOf("<xsl:for-each select=\"/root\">", 3, 2) +
                            makeValueOf("<item>", 4, 3) +
                "           <item/>" +
                "       </xsl:for-each>" +
                "   </xsl:template>" +
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    public void testCallTemplate() throws Exception {
        String originalStylesheet = "" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:param name='juxy:tracer'/>" +
                "   <xsl:template name='tpl'>\n" +
                "       <xsl:call-template name='tpl2'>\n" +
                "           <xsl:with-param name='p1' select='v1'/>\n" +
                "       </xsl:call-template>\n" +
                "   </xsl:template>\n" +
                "   <xsl:template name='tpl2'>\n" +
                "       <xsl:param name='p1'/>\n" +
                "   </xsl:template>\n" +
                "</xsl:stylesheet>";
        String filtered = filter(originalStylesheet);
        XMLComparator.assertEquals("" +
                "<xsl:stylesheet version='1.0' " +
                "   xmlns:juxy='http://juxy.tigris.org/' " +
                "   xmlns:tracer='java:org.tigris.juxy.Tracer'" +
                "   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "   <xsl:param name='juxy:tracer'/>" +
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
                "</xsl:stylesheet>", filtered);
        createTransformer(filtered);
    }

    private String makeValueOf(String statement, int line, int col) {
        String escapedStatement = StringUtil.escapeQuoteCharacter(
                StringUtil.escapeXMLText(StringUtil.replaceCharByEntityRef(statement, '\'')));
        return "<xsl:value-of select=\"tracer:trace($juxy:tracer, " + line + ", " + col + ", '" + getSystemId() + "', '" + escapedStatement + "')\"/>";
    }

    private String filter(String originalStylesheet) throws Exception {
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

        return bos.toString();
    }

    private String getSystemId() {
        return new StreamSource(new File("stylesheet.xsl")).getSystemId();
    }

    private void createTransformer(String stylesheet) throws TransformerConfigurationException {
        transformerFactory.newTransformer(new StreamSource(new ByteArrayInputStream(stylesheet.getBytes())));
    }
}
