package org.tigris.juxy.xpath;

import junit.framework.TestCase;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.DOMUtil;

/**
 * $Id: UTestJavaxXPathExpr.java,v 1.3 2006-10-31 11:01:24 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestJavaxXPathExpr extends TestCase {
  public void testAddNamespace() throws Exception {
    new JavaxXPathExpr("/xsl:template[ax:root]").addNamespace("xsl", XSLTKeys.XSLT_NS)
        .addNamespace("ss", "http://juxy.tigris.org");
  }

  public void testEmptyDocument() throws Exception {
    try {
      new JavaxXPathExpr("true()").toBoolean(null);
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }

    try {
      new JavaxXPathExpr("true()").toBoolean(DOMUtil.newDocument());
    }
    catch (IllegalArgumentException ex) {
    }
  }

  public void testToBoolean() throws Exception {
    assertTrue(new JavaxXPathExpr("true()").toBoolean(DOMUtil.parse("<source/>")));
    assertFalse(new JavaxXPathExpr("false()").toBoolean(DOMUtil.parse("<source/>")));
    assertTrue(new JavaxXPathExpr("source").toBoolean(DOMUtil.parse("<source/>")));
    assertFalse(new JavaxXPathExpr("root").toBoolean(DOMUtil.parse("<source/>")));
    assertTrue(new JavaxXPathExpr("source/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")));
    assertFalse(new JavaxXPathExpr("//root/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")));
  }

  public void testToString() throws Exception {
    assertEquals("", new JavaxXPathExpr("source").toString(DOMUtil.parse("<source/>")));
    assertEquals("ggg", new JavaxXPathExpr("'ggg'").toString(DOMUtil.parse("<source/>")));
    assertEquals("aaa", new JavaxXPathExpr("source").toString(DOMUtil.parse("<source>aaa</source>")));
    assertEquals("aaa", new JavaxXPathExpr("string('aaa')").toString(DOMUtil.parse("<source/>")));
  }

  public void testToNumber() throws Exception {
    assertEquals(3, new JavaxXPathExpr("1 + 2").toInt(DOMUtil.parse("<source/>")));
    assertEquals(1, new JavaxXPathExpr("count(source)").toInt(DOMUtil.parse("<source/>")));
    assertEquals(3.5, new JavaxXPathExpr("1 + 2.5").toDouble(DOMUtil.parse("<source/>")), 0.001);
  }

  public void testToNodeList() throws Exception {
    assertEquals(0, new JavaxXPathExpr("root").toNodeList(DOMUtil.parse("<source/>")).size());
    assertEquals(1, new JavaxXPathExpr("source").toNodeList(DOMUtil.parse("<source/>")).size());
    assertEquals(2, new JavaxXPathExpr("//source").toNodeList(DOMUtil.parse("<root><source/><source/></root>")).size());
  }

  public void testToNode() throws Exception {
    assertNull(new JavaxXPathExpr("root").toNode(DOMUtil.parse("<source/>")));
    assertEquals("source", new JavaxXPathExpr("source").toNode(DOMUtil.parse("<source/>")).getNodeName());
  }
}
