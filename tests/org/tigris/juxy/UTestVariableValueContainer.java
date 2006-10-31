package org.tigris.juxy;

import junit.framework.TestCase;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.tigris.juxy.xpath.XPathFactory;
import org.xml.sax.SAXException;

public class UTestVariableValueContainer extends TestCase {
  public void testValue() throws SAXException, XPathExpressionException {
    VariableValueContainer c = new VariableValueContainer("   ");
    assertEquals("   ", c.getStringValue());

    XPathExpr xp = XPathFactory.newXPath("/root");
    c = new VariableValueContainer(xp);
    assertTrue(c.isXPathValue());
    assertEquals(xp.getExpression(), c.getXPathValue());

    c = new VariableValueContainer(DOMUtil.newDocument());
    assertFalse(c.isNotEmptyContent());

    c = new VariableValueContainer(DOMUtil.parse("<page/>"));
    assertTrue(c.isNotEmptyContent());
  }
}
