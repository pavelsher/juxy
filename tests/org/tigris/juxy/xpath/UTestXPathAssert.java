package org.tigris.juxy.xpath;

import junit.framework.TestCase;
import org.tigris.juxy.util.DOMUtil;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Pavel.Sher
 *         Date: 19.10.2006
 */
public class UTestXPathAssert extends TestCase {
    public void testAssertDouble() throws SAXException, XPathExpressionException {
      Node node = DOMUtil.parse("<value>5.002</value>");
      new XPathAssert("/value/text()", 5.0, 0.005).eval(node);
      new XPathAssert("/value/text()", 5.1, 0.1).eval(node);
      new XPathAssert("/value/text()", 4.91, 0.1).eval(node);
      try {
        new XPathAssert("/value/text()", 4.9, 0.1).eval(node);
        fail("An exception expected");
      } catch (AssertionError error) {
//      error.printStackTrace();
      }
    }

    public void testAssertString() throws SAXException, XPathExpressionException {
        Node node = DOMUtil.parse("<value>\t\nsome text   with  spaces  \n</value>");

        new XPathAssert("/value/text()", "some text with spaces", true).eval(node);
        try {
            new XPathAssert("/value/text()", "some text with spaces", false).eval(node);
            fail("An exception expected");
        } catch (AssertionError assertionError) {}

        try {
            new XPathAssert("/value/text()", "some text with spaces").eval(node);
            fail("An exception expected");
        } catch (AssertionError assertionError) {}

        new XPathAssert("/value/text()", "\t\nsome text   with  spaces  \n").eval(node);
        new XPathAssert("/value/text()", "\t\nsome text   with  spaces  \n", false).eval(node);
    }
}
