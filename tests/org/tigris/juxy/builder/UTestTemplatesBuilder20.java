package org.tigris.juxy.builder;

import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.TestUtil;
import junit.framework.TestSuite;

/**
 * $Id: UTestTemplatesBuilder20.java,v 1.5 2006-11-09 17:28:06 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTemplatesBuilder20 extends BaseTestTemplatesBuilder {
  public UTestTemplatesBuilder20(String name) {
    super(name);
  }

  public static TestSuite suite() {
    if (!TestUtil.isXSLT20Supported()) {
      return new TestSuite();
    }

    return new TestSuite(UTestTemplatesBuilder20.class);
  }


  public void testXSLT20() throws Exception {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/xslt20.xsl"), null);
    builder.setInvokationStatementInfo(xpath("/"), null, null);
    builder.build();

    assertEquals("2.0", xpath("/xsl:stylesheet/@version")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toString(builder.getCurrentStylesheetDoc()));
  }
}
