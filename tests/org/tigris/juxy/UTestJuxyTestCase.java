package org.tigris.juxy;

import org.w3c.dom.Node;
import org.w3c.dom.Document;

/**
 * $Id: UTestJuxyTestCase.java,v 1.3 2005-08-11 08:24:37 pavelsher Exp $
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

        ctx = RunnerFactory.newRunner().newRunnerContext("tests/xml/templates.xsl");
        setContext(ctx);
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

    public void testPrintAndParse() throws Exception {
        print(parse("<root/>"));
        String docContent = "" +
                        "<list>" +
                        "   <item>text1</item>" +
                        "   <item>text2</item>" +
                        "</list>";
        Document doc = parse(docContent);
        print(xpath("//item[1]").toNode(doc));

        assertXMLEquals(docContent, asString(doc));
    }

    public void testNormalization() {
        assertEquals("bb aa cc", normalizeAll("\n\n   bb\naa  \n\tcc"));
        assertEquals("bb\naa \n cc", normalizeSpaces("\n\n   bb\naa  \n\tcc"));
    }
}
