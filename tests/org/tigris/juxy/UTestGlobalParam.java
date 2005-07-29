package org.tigris.juxy;

import junit.framework.TestCase;

public class UTestGlobalParam extends TestCase
{
    public UTestGlobalParam(String string)
    {
        super(string);
    }

    public void testConstructor()
    {
        try
        {
            new GlobalParam(null, "value");
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {};

        try
        {
            new GlobalParam("name", null);
            fail("An exception expected");
        }
        catch (IllegalArgumentException ex) {};

        new GlobalParam("name", "value");
    }

    public void testPrefixAndLocalName()
    {
        GlobalParam par = new GlobalParam("ss:name", "value");
        assertEquals("ss", par.getNamePrefix());
        assertEquals("name", par.getLocalName());
        assertTrue(par.hasPrefix());

        par = new GlobalParam("name", "value");
        assertEquals("", par.getNamePrefix());
        assertEquals("name", par.getLocalName());
        assertFalse(par.hasPrefix());

        par = new GlobalParam(":name", "value");
        assertEquals("", par.getNamePrefix());
        assertEquals("name", par.getLocalName());
        assertFalse(par.hasPrefix());
    }
}
