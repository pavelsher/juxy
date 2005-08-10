package org.tigris.juxy.xpath;

import junit.framework.TestCase;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.DOMUtil;
import org.xml.sax.SAXException;

/**
 * $Id: UTestJava50XPathExpr.java,v 1.1 2005-08-10 08:57:18 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestJava50XPathExpr extends TestCase {
    public void testAddNamespace() throws XPathExpressionException
    {
        new Java50XPathExpr("/xsl:template[ax:root]").addNamespace("xsl", XSLTKeys.XSLT_NS)
                                           .addNamespace("ss", "http://juxy.tigris.org");
    }

    public void testEmptyDocument() throws XPathExpressionException
    {
        try
        {
            new Java50XPathExpr("true()").toBoolean(null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {};

        try
        {
            new Java50XPathExpr("true()").toBoolean(DOMUtil.newDocument());
        }
        catch (IllegalArgumentException ex) {};
    }

    public void testToBoolean() throws XPathExpressionException, SAXException
    {
        assertTrue( new Java50XPathExpr("true()").toBoolean(DOMUtil.parse("<source/>")) );
        assertFalse( new Java50XPathExpr("false()").toBoolean(DOMUtil.parse("<source/>")) );
        assertTrue( new Java50XPathExpr("source").toBoolean(DOMUtil.parse("<source/>")) );
        assertFalse( new Java50XPathExpr("root").toBoolean(DOMUtil.parse("<source/>")) );
        assertTrue( new Java50XPathExpr("source/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")) );
        assertFalse( new Java50XPathExpr("//root/text()").toBoolean(DOMUtil.parse("<source>aaa<root/></source>")) );
    }

    public void testToString() throws XPathExpressionException, SAXException
    {
        assertEquals("", new Java50XPathExpr("source").toString(DOMUtil.parse("<source/>")) );
        assertEquals("ggg", new Java50XPathExpr("'ggg'").toString(DOMUtil.parse("<source/>")) );
        assertEquals("aaa", new Java50XPathExpr("source").toString(DOMUtil.parse("<source>aaa</source>")) );
        assertEquals("aaa", new Java50XPathExpr("string('aaa')").toString(DOMUtil.parse("<source/>")) );
    }

    public void testToNumber() throws XPathExpressionException, SAXException
    {
        assertEquals(3, new Java50XPathExpr("1 + 2").toInt(DOMUtil.parse("<source/>")) );
        assertEquals(1, new Java50XPathExpr("count(source)").toInt(DOMUtil.parse("<source/>")) );
        assertEquals(3.5, new Java50XPathExpr("1 + 2.5").toDouble(DOMUtil.parse("<source/>")), 0.001);
    }

    public void testToNodeSet() throws XPathExpressionException, SAXException
    {
        assertEquals(0, new Java50XPathExpr("root").toNodeSet(DOMUtil.parse("<source/>")).size() );
        assertEquals(1, new Java50XPathExpr("source").toNodeSet(DOMUtil.parse("<source/>")).size() );
        assertEquals(2, new Java50XPathExpr("//source").toNodeSet(DOMUtil.parse("<root><source/><source/></root>")).size() );
    }

    public void testToNode() throws XPathExpressionException, SAXException
    {
        assertNull(new Java50XPathExpr("root").toNode(DOMUtil.parse("<source/>")));
        assertEquals("source", new Java50XPathExpr("source").toNode(DOMUtil.parse("<source/>")).getNodeName() );
    }
}
