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
  public void testAssertBoolean() throws SAXException, XPathExpressionException {
    Node node = DOMUtil.parse("<tag><subtag></subtag></tag>");
    new XPathAssert("/tag/subtag").eval(node);
    new XPathAssert("/tag/subtag", true).eval(node);
    new XPathAssert("/tag/subtag1", false).eval(node);

    try {
      new XPathAssert("/tag/subtag1").eval(node);
      fail("An exception expected");
    } catch (AssertionError error) {}

    try {
      new XPathAssert("/tag/subtag", false).eval(node);
      fail("An exception expected");
    } catch (AssertionError error) {}
  }

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
    } catch (AssertionError assertionError) {
    }

    try {
      new XPathAssert("/value/text()", "some text with spaces").eval(node);
      fail("An exception expected");
    } catch (AssertionError assertionError) {
    }

    new XPathAssert("/value/text()", "\t\nsome text   with  spaces  \n").eval(node);
    new XPathAssert("/value/text()", "\t\nsome text   with  spaces  \n", false).eval(node);
  }

  public void testNamespaces() throws SAXException, XPathExpressionException {
    Node doc = DOMUtil.parse("" +
        "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
        "    <xsl:template name=\"foo\">\n" +
        "        <xsl:call-template name=\"foo\"></xsl:call-template>\n" +
        "    </xsl:template>\n" +
        "</xsl:stylesheet>");
    XPathAssert xp =
        new XPathAssert("count(/xsl:stylesheet/xsl:template[@name='foo']/xsl:call-template[@name='foo'])", 1);
    xp.addNamespace("xsl", "http://www.w3.org/1999/XSL/Transform").eval(doc);
  }

  public void testAssertNode() throws SAXException, XPathExpressionException {
    Node node = DOMUtil.parse("" +
        "<ul>" +
        "   <li>" +
        "       <p>some text</p>" +
        "   </li>" +
        "</ul>");
    new XPathAssert("//p", DOMUtil.parse("<p>some text</p>")).eval(node);
    new XPathAssert("//p", DOMUtil.parse("<p>\nsome text\n</p>")).eval(node);

    try {
      new XPathAssert("//p", DOMUtil.parse("<p>different text</p>")).eval(node);
    } catch (AssertionError error) {
//            error.printStackTrace();
    }

  }
}
