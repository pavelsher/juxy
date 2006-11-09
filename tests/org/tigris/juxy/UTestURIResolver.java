package org.tigris.juxy;

import org.tigris.juxy.util.DOMUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

import junit.framework.TestSuite;

/**
 * @author Pavel Sher
 */
public class UTestURIResolver extends JuxyTestCase {

  public static TestSuite suite() {
    if (!TestUtil.isCustomURIResolverSupported()) {
      return new TestSuite();
    }

    return new TestSuite(UTestRunnerTracing.class);
  }

  public void testResolver_StreamSource() throws Exception {
    newContext("/virtual/xsl/file", new URIResolver() {
      public Source resolve(String href, String base) throws TransformerException {
        File file = new File("tests/xml/fake.xsl");
        try {
          return new StreamSource(file.getCanonicalFile().toURI().toString());
        } catch (IOException e) {
          throw new TransformerException(e);
        }
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
      public Source resolve(String href, String base) throws TransformerException {
        SAXSource src = new SAXSource();
        try {
          String systemId = new File("tests/xml/fake.xsl").getCanonicalFile().toURI().toString();
          src.setSystemId(systemId);
        } catch (IOException e) {
          throw new TransformerException(e);
        }
        return src;
      }
    });

    context().setDocument("<root/>");

    callTemplate("aname");
  }
}
