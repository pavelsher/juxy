package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.XSLTKeys;

/**
 * $Id: UTestTemplatesBuilder20.java,v 1.2 2005-08-07 16:43:16 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTemplatesBuilder20 extends BaseTestTemplatesBuilder {

    public void testXSLT20() throws Exception {
        builder.setImportSystemId(getTestingXsltSystemId("tests/xml/xslt20.xsl"), null);
        builder.setInvokationStatementInfo(new XPathExpr("/"), null, null);
        builder.build();

        assertEquals( "2.0", new XPathExpr("/xsl:stylesheet/@version")
                                    .addNamespace("xsl", XSLTKeys.XSLT_NS)
                                    .toString(builder.getCurrentStylesheetDoc()) );
    }
}
