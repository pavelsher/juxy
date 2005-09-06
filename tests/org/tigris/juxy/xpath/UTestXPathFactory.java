package org.tigris.juxy.xpath;

import junit.framework.TestCase;

/**
 * $Id: UTestXPathFactory.java,v 1.3 2005-09-06 16:50:03 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestXPathFactory extends TestCase {
    public void testCreateXPathWithEmptyExpression() {
        try {
            XPathFactory.newXPath("");
            fail("An exception expected");
        } catch(IllegalArgumentException e) {}
    }

    public void testCreateJaxenXPath() {
        XPathExpr xpath = XPathFactory.createJaxenXPath("/");
        assertTrue(xpath instanceof JaxenXPathExpr);
    }

    public void testCreateJavaXPath() {
        XPathExpr xpath = XPathFactory.createJavaxXPath("/");
        assertTrue(xpath instanceof JavaxXPathExpr);
    }
}
