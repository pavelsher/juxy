package org.tigris.juxy;

import org.w3c.dom.Node;

/**
 * $Id: UTestXSLT20.java,v 1.2 2006-10-31 11:01:22 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestXSLT20 extends JuxyTestCase {
  public void testSequence() throws Exception {
    newContext("tests/xml/xslt20.xsl");
    context().setDocument("<root/>");

    Node result = callTemplate("numbers");
    xpathAssert("text()", "1, 2, 3, 4, 5").eval(result);
  }
}
