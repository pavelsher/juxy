package org.tigris.juxy;

import org.tigris.juxy.validator.ValidationFailedException;
import org.tigris.juxy.xpath.XPathAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Pavel Sher
 */
public class UTestJuxyTestCase extends JuxyTestCase {
  public void testContext() throws Exception {
    try {
      context();
    } catch (IllegalStateException e) {
    }

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
    if (TestUtil.isOracleXDK()) {
      System.out.println(getName() + " skipped under Oracle XDK");
      return;
    }

    RunnerContext ctx = newContext("tests/xml/templates.xsl");
    ctx.setDocument("<root>text</root>");

    Node result = applyTemplates();
    assertXMLEquals("<result>text</result>", result);

    result = applyTemplates(xpath("/"));
    assertXMLEquals("<result>text</result>", result);

    result = applyTemplates(xpath("root"), "mode");
    assertXMLEquals("<result>text [with mode]</result>", result);

    ctx.setDocument("<commentParent><!-- comment --></commentParent>");
    result = applyTemplates();
    assertXMLEquals("<result>comment</result>", result);
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

  public void testXMLSchemaValidation() throws Exception {
    RunnerContext ctx = newContext("tests/xml/templates.xsl");
    ctx.setDocument("<root/>");
    Node result = callTemplate("getRoot");

    try {
      validateWithSchema(result, "tests/xml/validator/schema1.xml");
      fail("An exception expected");
    } catch (ValidationFailedException e) {
    }
  }

  public void testEvalXPathAssertions() throws Exception {
    final XPathAssert[] asserts = new XPathAssert[]{
        xpathAssert("count(//li)", 3),
        xpathAssert("count(//ul)", 1),
    };

    evalAssertions(
        parse("<ul><li/><li/><li/></ul>"),
        asserts);

    try {
      evalAssertions(
          parse("<root><ul><li/><li/><li/></ul><ul/></root>"),
          asserts);
      fail("An exception expected");
    } catch (AssertionError e) {
    }
  }

  public void testXPathAssertionsExceptions() throws Exception {
    XPathAssert explicitlyCreated = new XPathAssert("count(tag)", 10);
    XPathAssert createdByTestCase = xpathAssert("count(tag)", 10);

    try {
      explicitlyCreated.eval(parse("<ul><li/><li/><li/></ul>"));
      fail("An exception expected");
    } catch (AssertionError error) {
    }

    try {
      createdByTestCase.eval(parse("<ul><li/><li/><li/></ul>"));
      fail("An exception expected");
    } catch (AssertionError error) {
    }

    try {
      evalAssertions(parse("<ul><li/><li/><li/></ul>"), new XPathAssert[]{explicitlyCreated});
      fail("An exception expected");
    } catch (AssertionError error) {
    }
  }
}
