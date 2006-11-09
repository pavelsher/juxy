package org.tigris.juxy;

import org.w3c.dom.Node;

import java.io.File;
import java.util.TimeZone;

import junit.framework.TestSuite;

/**
 * @author Pavel Sher
 */
public class UTestExternalFunctions extends JuxyTestCase {
  public static TestSuite suite() {
    if (!TestUtil.isExternalJavaFunctionsSupported()) {
      return new TestSuite();
    }

    return new TestSuite(UTestExternalFunctions.class);
  }

  protected void setUp() throws Exception {
    newContext(new File("tests/xml/extfunc.xsl").toURI().toString());
  }

  public void testCallNamedTemplateWithGlobalParamsAndExtObject() throws Exception {
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