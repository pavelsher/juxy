package org.tigris.juxy.verifier;

import junit.framework.TestCase;
import org.tigris.juxy.TestUtil;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.SAXUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

/**
 */
public class UTestIncludeInstructionsHandler extends TestCase {
  public void testNoImportsAndIncludes() throws Exception {
    IncludeInstructionsHandler iih = new IncludeInstructionsHandler();
    parse("stylesheet.xsl", makeStylesheet(""), iih);
    assertEquals(0, iih.getHrefs().size());
  }

  public void testImportsAndIncludes() throws Exception {
    IncludeInstructionsHandler iih = new IncludeInstructionsHandler();
    parse("stylesheet.xsl",
        makeStylesheet("" +
            "<xsl:import href='import1.xsl'/>" +
            "<xsl:include href='include1.xsl'/>" +
            "<xsl:import href='import2.xsl'/>" +
            "<xsl:include href='include2.xsl'/>"), iih);
    assertEquals(4, iih.getHrefs().size());
    assertTrue(iih.getHrefs().contains("import1.xsl"));
    assertTrue(iih.getHrefs().contains("import2.xsl"));
    assertTrue(iih.getHrefs().contains("include1.xsl"));
    assertTrue(iih.getHrefs().contains("include2.xsl"));
  }

  public void testParsingStopped() throws Exception {
    IncludeInstructionsHandler iih = new IncludeInstructionsHandler();
    try {
      parse("stylesheet.xsl",
          makeStylesheet("" +
              "<xsl:import href='import1.xsl'/>" +
              "<xsl:template match='/'/>"), iih);
      fail("An exception expected");
    } catch (ParseStoppedException e) {
    }

    assertEquals(1, iih.getHrefs().size());
    assertTrue(iih.getHrefs().contains("import1.xsl"));
  }

  private String makeStylesheet(String internalContent) {
    return "<xsl:stylesheet version='1.0' xmlns:xsl='" + XSLTKeys.XSLT_NS + "'>" + internalContent + "</xsl:stylesheet>";
  }

  private void parse(String systemId, String xml, ContentHandler h) throws Exception {
    XMLReader reader = SAXUtil.newXMLReader();
    reader.setContentHandler(h);
    reader.parse(TestUtil.makeInputSource(systemId, xml));
  }
}
