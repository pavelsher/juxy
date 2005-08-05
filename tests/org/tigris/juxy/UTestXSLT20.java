package org.tigris.juxy;

import org.w3c.dom.Node;

/**
 * $Id: UTestXSLT20.java,v 1.1 2005-08-05 08:31:11 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestXSLT20 extends JuxyTestCase {
    public void testSequence() throws Exception {
        newContext("tests/xml/xslt20.xsl");
        context().setDocument("<root/>");

        Node result = callTemplate("numbers");
        assertEquals("1, 2, 3, 4, 5", xpath("text()").toString(result).trim());
    }
}
