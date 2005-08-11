package org.tigris.juxy;

import org.w3c.dom.Node;

import java.util.TimeZone;

/**
 * $Id: UTestExternalFunctions.java,v 1.1 2005-08-11 08:24:37 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestExternalFunctions extends JuxyTestCase {
    protected void setUp() throws Exception {
        newContext("tests/xml/extfunc.xsl");
    }

    public void testCallNamedTemplateWithGlobalParamsAndExtObject() throws Exception
    {
        context().setDocument("<source/>");

        context().setGlobalParamValue("tz", TimeZone.getDefault());
        Node result = callTemplate("getTimeZoneString");
        assertNotNull(result);

        assertEquals(TimeZone.getDefault().toString(), xpath("root").toString(result));
    }

    public void testGettingStringsFromTransformer() throws Exception {
        context().setDocument("<source/>");

        ValueContainer container = new ValueContainer();
        context().setGlobalParamValue("container", container);
        context().setTemplateParamValue("string", "this is just a string");
        callTemplate("setStringToContainer");

        assertEquals("this is just a string", container.getValue());
    }
}