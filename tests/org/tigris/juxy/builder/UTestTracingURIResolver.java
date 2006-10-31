package org.tigris.juxy.builder;

import junit.framework.TestCase;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.util.SAXUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

/**
 * $Id: UTestTracingURIResolver.java,v 1.2 2006-10-31 11:01:22 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTracingURIResolver extends TestCase {
  public void testOriginalResolverReturnedNull() throws TransformerException {
    TracingURIResolver resolver = new TracingURIResolver(new URIResolver() {
      public Source resolve(String href, String base) {
        return null;
      }
    });

    assertNull(resolver.resolve("href", "base"));
  }

  public void testDOMSource() throws TransformerException {
    TracingURIResolver resolver = new TracingURIResolver(new URIResolver() {
      public Source resolve(String href, String base) throws TransformerException {
        try {
          Document doc = DOMUtil.parse("<root/>");
          return new DOMSource(doc.getDocumentElement());
        } catch (SAXException e) {
          throw new TransformerException(e);
        }
      }
    });

    Source src = resolver.resolve("href", "base");
    assertTrue(src instanceof DOMSource);
    assertEquals("root", ((DOMSource) src).getNode().getNodeName());
  }

  public void testSAXSource_NoXMLReaderPassed() throws TransformerException {
    TracingURIResolver resolver = new TracingURIResolver(new URIResolver() {
      public Source resolve(String href, String base) {
        SAXSource src = new SAXSource();
        src.setSystemId("stylesheet.xsl");
        return src;
      }
    });

    Source src = resolver.resolve("href", "base");
    assertTrue(src instanceof SAXSource);
    SAXSource saxSrc = (SAXSource) src;
    assertEquals("stylesheet.xsl", saxSrc.getSystemId());
    XMLReader xmlReader = saxSrc.getXMLReader();
    assertNotNull(xmlReader);
    assertTrue(xmlReader instanceof TracingFilter);
  }

  public void testSAXSource_WasXMLReader() throws TransformerException {
    final XMLReader origReader = SAXUtil.newXMLReader();
    TracingURIResolver resolver = new TracingURIResolver(new URIResolver() {
      public Source resolve(String href, String base) {
        SAXSource src = new SAXSource();
        src.setSystemId("stylesheet.xsl");
        src.setXMLReader(origReader);
        return src;
      }
    });

    Source src = resolver.resolve("href", "base");
    assertTrue(src instanceof SAXSource);
    SAXSource saxSrc = (SAXSource) src;
    XMLReader xmlReader = saxSrc.getXMLReader();
    assertTrue(xmlReader instanceof TracingFilter);
    TracingFilter trFilter = (TracingFilter) xmlReader;
    assertSame(origReader, trFilter.getParent());
  }

  public void testStreamSource() throws TransformerException {
    TracingURIResolver resolver = new TracingURIResolver(new URIResolver() {
      public Source resolve(String href, String base) {
        StreamSource src = new StreamSource();
        src.setSystemId("stylesheet.xsl");
        return src;
      }
    });

    Source src = resolver.resolve("href", "base");
    assertTrue(src instanceof SAXSource);
    SAXSource saxSrc = (SAXSource) src;
    assertEquals("stylesheet.xsl", saxSrc.getSystemId());
  }
}
