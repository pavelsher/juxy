package org.tigris.juxy.builder;

import org.tigris.juxy.XSLTKeys;

/**
 * $Id: UTestTemplatesBuilder20.java,v 1.4 2006-10-31 11:01:22 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTemplatesBuilder20 extends BaseTestTemplatesBuilder {

  public void testXSLT20() throws Exception {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/xslt20.xsl"), null);
    builder.setInvokationStatementInfo(xpath("/"), null, null);
    builder.build();

    assertEquals("2.0", xpath("/xsl:stylesheet/@version")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toString(builder.getCurrentStylesheetDoc()));
  }
}
