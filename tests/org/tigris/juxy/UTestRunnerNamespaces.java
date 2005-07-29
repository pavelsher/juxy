package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import junit.framework.TestCase;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;

public class UTestRunnerNamespaces extends TestCase
{
    private Runner runner = null;

    public void setUp()
    {
        runner = RunnerFactory.newRunner();
    }

    public void testGlobalVariables() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/variable.xsl");
        ctx.setDocument("<source/>");
        ctx.registerNamespace("ns1", "http://ns1.net");
        ctx.setGlobalVariableValue("ns1:var", "avalue");

        Node result = runner.callTemplate(ctx, "getter");
        assertNotNull(result);

        assertEquals("avalue", new XPathExpr("root").toString(result));
    }

    public void testGlobalParams() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/param.xsl");
        ctx.setDocument("<source/>");
        ctx.registerNamespace("ns1", "http://ns1.net");
        ctx.setGlobalParamValue("ns1:par", "avalue");

        Node result = runner.callTemplate(ctx, "getter");
        assertNotNull(result);

        assertEquals("avalue", new XPathExpr("root").toString(result));
    }

    public void testInvokeParams() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/invoke.xsl");
        ctx.setDocument("<source/>");
        ctx.registerNamespace("ns1", "http://ns1.net");
        ctx.setTemplateParamValue("ns1:par", "avalue");

        Node result = runner.callTemplate(ctx, "getter");
        assertNotNull(result);

        assertEquals("avalue", new XPathExpr("root").toString(result));
    }

    public void testTemplates() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/templates.xsl");
        ctx.setDocument("<source/>");
        ctx.registerNamespace("ns1", "http://ns1.net");

        Node result = runner.callTemplate(ctx, "ns1:named");
        assertNotNull(result);
        assertNotNull( new XPathExpr("named").toString(result) );

        result = runner.applyTemplates(ctx, new XPathExpr("/"), "ns1:mode");
        assertNotNull(result);
        assertNotNull( new XPathExpr("matched").toString(result) );
    }
}
