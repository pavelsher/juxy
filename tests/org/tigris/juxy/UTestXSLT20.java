package org.tigris.juxy;

import org.w3c.dom.Node;
import junit.framework.TestSuite;

/**
 * $Id: UTestXSLT20.java,v 1.3 2006-11-09 17:28:06 pavelsher Exp $
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

  public void testSequence() throws Exception {
    newContext("tests/xml/xslt20.xsl");
    context().setDocument("<root/>");

    Node result = callTemplate("numbers");
    xpathAssert("text()", "1, 2, 3, 4, 5").eval(result);
  }
}
