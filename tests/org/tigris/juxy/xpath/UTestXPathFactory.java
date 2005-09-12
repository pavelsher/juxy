package org.tigris.juxy.xpath;

import junit.framework.TestCase;

import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathVariableResolver;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPath;

/**
 * $Id: UTestXPathFactory.java,v 1.4 2005-09-12 07:43:48 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestXPathFactory extends TestCase {
    protected void tearDown() throws Exception {
        XPathFactory.reset();
    }

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

    public void testFailedToCreateJavaxXPath() {
        String factoryProperty = "javax.xml.xpath.XPathFactory:" + javax.xml.xpath.XPathConstants.DOM_OBJECT_MODEL;
        System.setProperty(factoryProperty, FakeXPathFactory.class.getName());

        XPathExpr xpath = XPathFactory.newXPath("/");
        assertTrue(xpath instanceof JaxenXPathExpr);

        System.setProperty(factoryProperty, "");
    }

    public static class FakeXPathFactory extends javax.xml.xpath.XPathFactory {
        public boolean isObjectModelSupported(String objectModel) {
            return false;
        }

        public void setFeature(String name, boolean value) throws XPathFactoryConfigurationException {}

        public boolean getFeature(String name) throws XPathFactoryConfigurationException {
            return false;
        }

        public void setXPathVariableResolver(XPathVariableResolver resolver) {}

        public void setXPathFunctionResolver(XPathFunctionResolver resolver) {}

        public XPath newXPath() {
            throw new RuntimeException("not supported xpath");
        }
    }
}
