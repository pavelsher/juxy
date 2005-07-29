package org.tigris.juxy.xpath;

import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.DOMUtil;
import junit.framework.TestCase;
import org.xml.sax.SAXException;

public class UTestXPathExpr extends TestCase
{
    public UTestXPathExpr(String s)
    {
        super(s);
    }

    public void testConstructor()
    {
        try
        {
            new XPathExpr(null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {}
        catch (XPathExpressionException ex)
        {
            fail("This exception was not expected");
        }

        try
        {
            new XPathExpr("\\aa");
            fail("An exception expected");
        }
        catch (XPathExpressionException ex) {};
    }

    public void testAddNamespace() throws XPathExpressionException
    {
        new XPathExpr("/xsl:template[ax:root]").addNamespace("xsl", XSLTKeys.xslt10Namespace)
                                           .addNamespace("ss", "http://juxy.tigris.org");
    }

    public void testEmptyDocument() throws XPathExpressionException
    {
        try
        {
            new XPathExpr("true()").toBoolean(null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {};

        try
        {
            new XPathExpr("true()").toBoolean(DOMUtil.newDocument());
        }
        catch (IllegalArgumentException ex) {};
    }

    public void testToBoolean() throws XPathExpressionException, SAXException
    {
        assertTrue( new XPathExpr("true()").toBoolean(DOMUtil.parse("<source/>")) );
        assertFalse( new XPathExpr("false()").toBoolean(DOMUtil.parse("<source/>")) );
        assertTrue( new XPathExpr("source").toBoolean(DOMUtil.parse("<source/>")) );
        assertFalse( new XPathExpr("root").toBoolean(DOMUtil.parse("<source/>")) );
        assertTrue( new XPathExpr("source/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")) );
        assertFalse( new XPathExpr("//root/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")) );
    }

    public void testToString() throws XPathExpressionException, SAXException
    {
        assertEquals("", new XPathExpr("source").toString(DOMUtil.parse("<source/>")) );
        assertEquals("ggg", new XPathExpr("'ggg'").toString(DOMUtil.parse("<source/>")) );
        assertEquals("aaa", new XPathExpr("source").toString(DOMUtil.parse("<source>aaa</source>")) );
        assertEquals("aaa", new XPathExpr("string('aaa')").toString(DOMUtil.parse("<source/>")) );
    }

    public void testToNumber() throws XPathExpressionException, SAXException
    {
        assertEquals(3, new XPathExpr("1 + 2").toInt(DOMUtil.parse("<source/>")) );
        assertEquals(1, new XPathExpr("count(source)").toInt(DOMUtil.parse("<source/>")) );
        assertEquals(3.5, new XPathExpr("1 + 2.5").toDouble(DOMUtil.parse("<source/>")), 0.001);
    }

    public void testToNodeSet() throws XPathExpressionException, SAXException
    {
        assertEquals(0, new XPathExpr("root").toNodeSet(DOMUtil.parse("<source/>")).size() );
        assertEquals(1, new XPathExpr("source").toNodeSet(DOMUtil.parse("<source/>")).size() );
        assertEquals(2, new XPathExpr("//source").toNodeSet(DOMUtil.parse("<root><source/><source/></root>")).size() );
    }
}
