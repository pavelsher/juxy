package org.tigris.juxy;

import junit.framework.TestCase;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathFactory;
import org.tigris.juxy.xpath.XPathExpr;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import java.io.File;

public class UTestRunnerContext extends TestCase
{
    private RunnerContextImpl ctx = null;

    public void setUp()
    {
        ctx = new RunnerContextImpl("somefile.xsl");
    }

    public void testSetDocument() throws SAXException {
        try
        {
            ctx.setDocument((String)null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {};

        try
        {
            ctx.setDocument("        ");
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {};

        ctx.setDocument("<root/>");
        assertTrue(ctx.getSourceDocument() instanceof SAXSource);

        ctx.setDocument(DOMUtil.parse("<root/>"));
        assertTrue(ctx.getSourceDocument() instanceof DOMSource);

        ctx.setDocument(new File("xml/document.xml"));
        assertTrue(ctx.getSourceDocument() instanceof StreamSource);
    }

    public void testSetCurrentNode() {
        XPathExpr xpathExpr = XPathFactory.newXPath("/");
        ctx.setCurrentNode(xpathExpr);
        assertSame(xpathExpr, ctx.getCurrentNodeSelector());
    }

    public void testSetGlobalParam() {
        assertEquals(0, ctx.getGlobalParams().size());

        Object value = new Object();

        ctx.setGlobalParamValue("param", value);
        assertEquals(1, ctx.getGlobalParams().size());
        assertTrue(ctx.getGlobalParams().contains(new GlobalParam("param", value)));

        ctx.setGlobalParamValue("param2", value);
        assertEquals(2, ctx.getGlobalParams().size());
        assertTrue(ctx.getGlobalParams().contains(new GlobalParam("param2", value)));
    }

    public void testSetGlobalVariable() throws SAXException {
        assertEquals(0, ctx.getGlobalVariables().size());

        ctx.setGlobalVariableValue("var1", "value1");
        ctx.setGlobalVariableValue("var2", XPathFactory.newXPath("/"));
        Document varContent = DOMUtil.parse("<root/>");
        ctx.setGlobalVariableValue("var3", varContent);

        assertEquals(3, ctx.getGlobalVariables().size());
        assertTrue(ctx.getGlobalVariables().contains(new GlobalVariable("var1", "value1")));
        assertTrue(ctx.getGlobalVariables().contains(new GlobalVariable("var2", XPathFactory.newXPath("/"))));
        assertTrue(ctx.getGlobalVariables().contains(new GlobalVariable("var3", varContent)));
    }

    public void testSetTemplateParam() throws SAXException {
        assertEquals(0, ctx.getTemplateParams().size());

        ctx.setTemplateParamValue("var1", "value1");
        ctx.setTemplateParamValue("var2", XPathFactory.newXPath("/"));
        Document paramContent = DOMUtil.parse("<root/>");
        ctx.setTemplateParamValue("var3", paramContent);

        assertEquals(3, ctx.getTemplateParams().size());
        assertTrue(ctx.getTemplateParams().contains(new InvokeParam("var1", "value1")));
        assertTrue(ctx.getTemplateParams().contains(new InvokeParam("var2", XPathFactory.newXPath("/"))));
        assertTrue(ctx.getTemplateParams().contains(new InvokeParam("var3", paramContent)));
    }

    public void testClearParamsAndVariables()
    {
        ctx.setGlobalParamValue("aname", "avalue");
        assertEquals(1, ctx.getGlobalParams().size());
        ctx.clearGlobalParams();
        assertEquals(0, ctx.getGlobalParams().size());

        ctx.setGlobalVariableValue("aname", "avalue");
        assertEquals(1, ctx.getGlobalVariables().size());
        ctx.clearGlobalVariables();
        assertEquals(0, ctx.getGlobalVariables().size());

        ctx.setTemplateParamValue("aname", "avalue");
        assertEquals(1, ctx.getTemplateParams().size());
        ctx.clearTemplateParams();
        assertEquals(0, ctx.getTemplateParams().size());
    }

    public void testParamsAndVariablesRewriting()
    {
        ctx.setGlobalParamValue("aname", "avalue1");
        ctx.setGlobalParamValue("aname", "avalue2");
        assertEquals(1, ctx.getGlobalParams().size());
        GlobalParam par = (GlobalParam)ctx.getGlobalParams().toArray(new GlobalParam[0])[0];
        assertEquals("avalue2", par.getValue());

        ctx.setGlobalVariableValue("aname", "avalue1");
        ctx.setGlobalVariableValue("aname", "avalue2");
        assertEquals(1, ctx.getGlobalVariables().size());
        VariableBase var = (VariableBase)ctx.getGlobalVariables().toArray(new VariableBase[0])[0];
        assertEquals("avalue2", var.getStringValue());

        ctx.setTemplateParamValue("aname", "avalue1");
        ctx.setTemplateParamValue("aname", "avalue2");
        assertEquals(1, ctx.getTemplateParams().size());
        VariableBase p = (VariableBase)ctx.getTemplateParams().toArray(new VariableBase[0])[0];
        assertEquals("avalue2", p.getStringValue());
    }

    public void testRegisterNamespaces() {
        ctx.registerNamespace("", "http://ns1.net");
        assertEquals(1, ctx.getNamespaces().size());
        assertEquals("", ctx.getNamespaces().get("http://ns1.net"));

        ctx.registerNamespace("prefix", "http://ns1.net");
        assertEquals(1, ctx.getNamespaces().size());
        assertEquals("prefix", ctx.getNamespaces().get("http://ns1.net"));

        ctx.registerNamespace("prefix", "http://ns2.net");
        assertEquals(2, ctx.getNamespaces().size());
        assertEquals("prefix", ctx.getNamespaces().get("http://ns2.net"));

        ctx.registerNamespace("prefix2", "http://ns2.net");
        assertEquals(2, ctx.getNamespaces().size());
        assertEquals("prefix", ctx.getNamespaces().get("http://ns1.net"));
        assertEquals("prefix2", ctx.getNamespaces().get("http://ns2.net"));

        ctx.clearNamespaces();
        assertEquals(0, ctx.getNamespaces().size());
    }
}
