package org.tigris.juxy.xpath;

import junit.framework.TestCase;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.DOMUtil;
import org.xml.sax.SAXException;

public class UTestJaxenXPathExpr extends TestCase {
  public void testAddNamespace() throws XPathExpressionException {
    new JaxenXPathExpr("/xsl:template[ax:root]").addNamespace("xsl", XSLTKeys.XSLT_NS)
        .addNamespace("ss", "http://juxy.tigris.org");
  }

  public void testEmptyDocument() throws XPathExpressionException {
    try {
      new JaxenXPathExpr("true()").toBoolean(null);
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }

    try {
      new JaxenXPathExpr("true()").toBoolean(DOMUtil.newDocument());
    }
    catch (IllegalArgumentException ex) {
    }
  }

  public void testToBoolean() throws XPathExpressionException, SAXException {
    assertTrue(new JaxenXPathExpr("true()").toBoolean(DOMUtil.parse("<source/>")));
    assertFalse(new JaxenXPathExpr("false()").toBoolean(DOMUtil.parse("<source/>")));
    assertTrue(new JaxenXPathExpr("source").toBoolean(DOMUtil.parse("<source/>")));
    assertFalse(new JaxenXPathExpr("root").toBoolean(DOMUtil.parse("<source/>")));
    assertTrue(new JaxenXPathExpr("source/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")));
    assertFalse(new JaxenXPathExpr("//root/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")));
  }

  public void testToString() throws XPathExpressionException, SAXException {
    assertEquals("", new JaxenXPathExpr("source").toString(DOMUtil.parse("<source/>")));
    assertEquals("ggg", new JaxenXPathExpr("'ggg'").toString(DOMUtil.parse("<source/>")));
    assertEquals("aaa", new JaxenXPathExpr("source").toString(DOMUtil.parse("<source>aaa</source>")));
    assertEquals("aaa", new JaxenXPathExpr("string('aaa')").toString(DOMUtil.parse("<source/>")));
  }

  public void testToNumber() throws XPathExpressionException, SAXException {
    assertEquals(3, new JaxenXPathExpr("1 + 2").toInt(DOMUtil.parse("<source/>")));
    assertEquals(1, new JaxenXPathExpr("count(source)").toInt(DOMUtil.parse("<source/>")));
    assertEquals(3.5, new JaxenXPathExpr("1 + 2.5").toDouble(DOMUtil.parse("<source/>")), 0.001);
  }

  public void testToNodeList() throws XPathExpressionException, SAXException {
    assertEquals(0, new JaxenXPathExpr("root").toNodeList(DOMUtil.parse("<source/>")).size());
    assertEquals(1, new JaxenXPathExpr("source").toNodeList(DOMUtil.parse("<source/>")).size());
    assertEquals(2, new JaxenXPathExpr("//source").toNodeList(DOMUtil.parse("<root><source/><source/></root>")).size());
  }

  public void testToNode() throws XPathExpressionException, SAXException {
    assertNull(new JaxenXPathExpr("root").toNode(DOMUtil.parse("<source/>")));
    assertEquals("source", new JaxenXPathExpr("source").toNode(DOMUtil.parse("<source/>")).getNodeName());
  }
}
