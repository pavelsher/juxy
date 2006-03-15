package org.tigris.juxy;

import org.w3c.dom.Node;

import java.util.TimeZone;
import java.io.File;

/**
 * $Id: UTestExternalFunctions.java,v 1.2 2006-03-15 09:56:26 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestExternalFunctions extends JuxyTestCase {
    protected void setUp() throws Exception {
        newContext(new File("tests/xml/extfunc.xsl").toURI().toString());
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