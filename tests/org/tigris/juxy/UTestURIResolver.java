package org.tigris.juxy;

import org.tigris.juxy.util.DOMUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * $Id: UTestURIResolver.java,v 1.1 2005-08-07 16:43:16 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestURIResolver extends JuxyTestCase {
    public void testResolver_StreamSource() throws Exception {
        newContext("/virtual/xsl/file", new URIResolver() {
            public Source resolve(String href, String base) {
                return new StreamSource(new File("tests/xml/fake.xsl"));
            }
        });

        context().setDocument("<root/>");

        callTemplate("aname");
    }

    public void testResolver_DOMSource() throws Exception {
        newContext("/virtual/xsl/file", new URIResolver() {
            public Source resolve(String href, String base) {
                try {
                    Document doc = DOMUtil.parse("" +
                            "<xsl:stylesheet version='2.0' xmlns:xsl='" + XSLTKeys.XSLT_NS + "'>" +
                            "<xsl:template name='tpl'/>" +
                            "</xsl:stylesheet>");
                    return new DOMSource(doc, "/some/system/id");
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        context().setDocument("<root/>");

        callTemplate("tpl");
    }

    public void testResolver_SAXSource() throws Exception {
        newContext("/virtual/xsl/file", new URIResolver() {
            public Source resolve(String href, String base) {
                SAXSource src = new SAXSource();
                src.setSystemId("tests/xml/fake.xsl");
                return src;
            }
        });

        context().setDocument("<root/>");

        callTemplate("aname");
    }
}
