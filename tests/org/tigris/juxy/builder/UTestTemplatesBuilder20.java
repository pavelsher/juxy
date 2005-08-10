package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.JaxenXPathExpr;
import org.tigris.juxy.XSLTKeys;

/**
 * $Id: UTestTemplatesBuilder20.java,v 1.3 2005-08-10 08:57:18 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTemplatesBuilder20 extends BaseTestTemplatesBuilder {

    public void testXSLT20() throws Exception {
        builder.setImportSystemId(getTestingXsltSystemId("tests/xml/xslt20.xsl"), null);
        builder.setInvokationStatementInfo(xpath("/"), null, null);
        builder.build();

        assertEquals( "2.0", xpath("/xsl:stylesheet/@version")
                                    .addNamespace("xsl", XSLTKeys.XSLT_NS)
                                    .toString(builder.getCurrentStylesheetDoc()) );
    }
}
