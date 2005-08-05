package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.XSLTKeys;

/**
 * $Id: UTestTemplatesBuilder20.java,v 1.1 2005-08-05 08:31:11 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTemplatesBuilder20 extends BaseTestTemplatesBuilder {

    public void testXSLT20() throws Exception {
        builder.setImportSystemId(getTestingXsltSystemId("tests/xml/xslt20.xsl"));
        builder.setInvokationStatementInfo(new XPathExpr("/"), null, null);
        builder.build();

        assertEquals( "2.0", new XPathExpr("/xsl:stylesheet/@version")
                                    .addNamespace("xsl", XSLTKeys.XSLT_NS)
                                    .toString(builder.getCurrentStylesheetDoc()) );
    }
}
