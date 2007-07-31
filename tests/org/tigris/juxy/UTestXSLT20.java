package org.tigris.juxy;

import junit.framework.TestSuite;
import org.tigris.juxy.util.DOMUtil;
import org.w3c.dom.Node;

/**
 * $Id: UTestXSLT20.java,v 1.4 2007-07-31 08:24:16 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestXSLT20 extends JuxyTestCase {
  public UTestXSLT20(String name) {
    super(name);
  }

  public static TestSuite suite() {
    if (!TestUtil.isXSLT20Supported()) {
      return new TestSuite();
    }

    return new TestSuite(UTestXSLT20.class);
  }

  protected void setUp() throws Exception {
    newContext("tests/xml/xslt20.xsl");
    context().setDocument("<root/>");
  }

  public void testSequence() throws Exception {
    Node result = callTemplate("numbers");
    xpathAssert("text()", "1, 2, 3, 4, 5").eval(result);
  }

  public void testFunction() throws Exception {
    Node result = callTemplate("function");
    assertEquals("5", DOMUtil.innerText(result));
  }
}
