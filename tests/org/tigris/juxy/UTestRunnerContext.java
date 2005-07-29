package org.tigris.juxy;

import junit.framework.TestCase;

public class UTestRunnerContext extends TestCase
{
    public void setUp()
    {
        ctx = new RunnerContextImpl("somefile.xsl");
    }

    public void testNullSourceDoc()
    {
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
    }

    public void testClearParams()
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

    public void testParamsRewriting()
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

    private RunnerContextImpl ctx = null;
}
