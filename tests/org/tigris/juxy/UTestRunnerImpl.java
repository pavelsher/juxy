package org.tigris.juxy;

import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import junit.framework.TestCase;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.util.TimeZone;

public class UTestRunnerImpl extends TestCase
{
    public void setUp()
    {
        runner = RunnerFactory.newRunner();
    }

    public void testFileNotExists()
    {
        try
        {
            runner.newRunnerContext("afile");
            fail("An exception expected");
        }
        catch (FileNotFoundException e) {}
    }

    public void testCallTemplateWithNullContext() throws TransformerException {
        try
        {
            runner.callTemplate(null, "aname");
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {}
    }

    public void testApplyTemplatesWithNullContext() throws XPathExpressionException, TransformerException
    {
        try
        {
            runner.applyTemplates(null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {}

        try
        {
            runner.applyTemplates(null, new XPathExpr("aselect"));
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {}

        try
        {
            runner.applyTemplates(null, new XPathExpr("aselect"), "amode");
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {}
    }

    public void testCallTemplateWithNullName() throws FileNotFoundException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/fake.xsl");
        ctx.setDocument("<page/>");
        try
        {
            runner.callTemplate(ctx, null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {}
    }

    public void testCallTemplateWithoutSourceDoc() throws FileNotFoundException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/fake.xsl");
        try
        {
            runner.callTemplate(ctx, "aname");
            fail("An exception expected");
        }
        catch (IllegalStateException ex) {}
    }

    public void testApplyTemplateWithoutSourceDoc() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/fake.xsl");
        try
        {
            runner.applyTemplates(ctx);
            fail("An exception expected");
        }
        catch (IllegalStateException ex) {}

        try
        {
            runner.applyTemplates(ctx, new XPathExpr("aselect"));
            fail("An exception expected");
        }
        catch (IllegalStateException ex) {}

        try
        {
            runner.applyTemplates(ctx, new XPathExpr("aselect"), "amode");
            fail("An exception expected");
        }
        catch (IllegalStateException ex) {}
    }


    public void testCallNamedTemplate() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/name-tpl.xsl");
        ctx.setDocument("<source/>");
        Node result = runner.callTemplate(ctx, "getText");
        assertNotNull(result);

        assertEquals("atext", new XPathExpr("root/text()").toString(result));
    }

    public void testCallNamedTemplateWithGlobalParams() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/name-tpl.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.callTemplate(ctx, "getGlobalParamValue");
        assertNotNull(result);

        assertEquals("", new XPathExpr("/root").toString(result));

        ctx.setGlobalParamValue("aparam", "avalue");
        result = runner.callTemplate(ctx, "getGlobalParamValue");
        assertNotNull(result);

        assertEquals("avalue", new XPathExpr("root").toString(result));
    }

    public void testCallNamedTemplateWithGlobalParamsAndExtObject() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/extfunc.xsl");
        ctx.setDocument("<source/>");

        ctx.setGlobalParamValue("tz", TimeZone.getDefault());
        Node result = runner.callTemplate(ctx, "getTimeZoneString");
        assertNotNull(result);

        assertEquals(TimeZone.getDefault().toString(), new XPathExpr("root").toString(result));
    }

    public void testCallNamedTemplateWithInvokeParam() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/name-tpl.xsl");
        ctx.setDocument("<source/>");

        ctx.setTemplateParamValue("invparam1", "1");
        ctx.setTemplateParamValue("invparam2", "2");

        Node result = runner.callTemplate(ctx, "getConcatenatedInvokeParamValues");
        assertNotNull(result);

        assertEquals("1:2", new XPathExpr("root").toString(result));

        result = runner.callTemplate(ctx, "getSumOfInvokeParamValues");
        assertNotNull(result);

        assertEquals(3, new XPathExpr("root").toInt(result));
    }

    public void testGlobalVariablesDefaultValues() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/variables.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.callTemplate(ctx, "getVarWithStringValue");
        assertNotNull(result);

        assertEquals("defaultvalue", new XPathExpr("root").toString(result));

        result = runner.callTemplate(ctx, "getVarWithSelectValue");
        assertNotNull(result);

        assertNotNull(new XPathExpr("*[1][self::source]").toNode(result));
        assertEquals(1, new XPathExpr("count(*)").toInt(result));

        result = runner.callTemplate(ctx, "getVarWithContentValue");
        assertNotNull(result);

        assertNotNull(new XPathExpr("*[1][self::rootElem]").toNode(result));
        assertEquals(1, new XPathExpr("count(*)").toInt(result));
    }

    public void testGlobalVariablesRedefined() throws FileNotFoundException, SAXException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/variables.xsl");
        ctx.setDocument("<source><subElem/></source>");

        ctx.setGlobalVariableValue("varWithString", (String)null);
        Node result = runner.callTemplate(ctx, "getVarWithStringValue");
        assertNotNull(result);

        assertEquals("", new XPathExpr("text()").toString(result));

        ctx.setGlobalVariableValue("varWithString", "new value");
        result = runner.callTemplate(ctx, "getVarWithStringValue");
        assertNotNull(result);

        assertEquals("new value", new XPathExpr("root").toString(result));

        ctx.setGlobalVariableValue("varWithSelect", new XPathExpr("//subElem"));
        result = runner.callTemplate(ctx, "getVarWithSelectValue");
        assertNotNull(result);

        assertNotNull(new XPathExpr("*[1][self::subElem]").toNode(result));
        assertEquals(1, new XPathExpr("count(*)").toInt(result));

        ctx.setGlobalVariableValue("varWithContent", DOMUtil.parse("<varContent/>"));
        result = runner.callTemplate(ctx, "getVarWithContentValue");
        assertNotNull(result);

        assertNotNull(new XPathExpr("*[1][self::varContent]").toNode(result));
        assertEquals(1, new XPathExpr("count(*)").toInt(result));
    }

    public void testRelativeImportWorks() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/relative-import.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.applyTemplates(ctx);
        assertNotNull(result);

        assertNotNull(new XPathExpr("root").toNode(result));
    }

    public void testRelativeIncludeWorks() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/relative-include.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.applyTemplates(ctx);
        assertNotNull(result);

        assertNotNull(new XPathExpr("root").toNode(result));
    }

    public void testRelativeDocumentFunctionWorks() throws FileNotFoundException, XPathExpressionException, TransformerException
    {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/document-func.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.callTemplate(ctx, "copyDoc");
        assertNotNull(result);

        assertNotNull(new XPathExpr("document").toNode(result));
    }

    public void testTextOnlyOutput() throws FileNotFoundException, TransformerException, XPathExpressionException {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/not-xml-output.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.callTemplate(ctx, "textOnly");
        assertEquals("The result of this template is this text.", new XPathExpr("text()").toString(result).trim());
    }

    public void testMoreThanOneRootElement() throws FileNotFoundException, TransformerException, XPathExpressionException {
        RunnerContext ctx = runner.newRunnerContext("tests/xml/not-xml-output.xsl");
        ctx.setDocument("<source/>");

        Node result = runner.callTemplate(ctx, "moreThanOneRoot");
        assertEquals(2, new XPathExpr("count(root)").toInt(result));
    }

    private Runner runner = null;
}
