package org.tigris.juxy;

import org.w3c.dom.Node;

/**
 * $Id: UTestJuxyTestCase.java,v 1.2 2005-07-30 10:51:43 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestJuxyTestCase extends JuxyTestCase {
    public void testContext() throws Exception {
        try {
            context();
        } catch (IllegalStateException e) {}

        RunnerContext ctx = newContext("tests/xml/templates.xsl");
        assertSame(context(), ctx);
    }

    public void testCallTemplate() throws Exception {
        RunnerContext ctx = newContext("tests/xml/templates.xsl");
        ctx.setDocument("<root/>");
        Node result = callTemplate("getRoot");
        assertXMLEquals("<result><root/></result>", result);
    }

    public void testApplyTemplates() throws Exception {
        RunnerContext ctx = newContext("tests/xml/templates.xsl");
        ctx.setDocument("<root>text</root>");

        Node result = applyTemplates();
        assertXMLEquals("<result>text</result>", result);

        result = applyTemplates(xpath("/"));
        assertXMLEquals("<result>text</result>", result);

        result = applyTemplates(xpath("root"), "mode");
        assertXMLEquals("<result>text [with mode]</result>", result);
    }
}
