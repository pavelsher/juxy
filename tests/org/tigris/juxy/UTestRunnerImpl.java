package org.tigris.juxy;

import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.util.XMLComparator;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;

public class UTestRunnerImpl extends JuxyTestCase {
  public void setUp() {
    runner = RunnerFactory.newRunner();
  }

  public void testCallTemplateWithNullContext() throws TransformerException {
    try {
      runner.callTemplate(null, "aname");
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }
  }

  public void testApplyTemplatesWithNullContext() throws Exception {
    try {
      runner.applyTemplates(null);
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }

    try {
      runner.applyTemplates(null, xpath("aselect"));
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }

    try {
      runner.applyTemplates(null, xpath("aselect"), "amode");
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }
  }

  public void testCallTemplateWithNullName() throws FileNotFoundException, TransformerException {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/fake.xsl");
    ctx.setDocument("<page/>");
    try {
      runner.callTemplate(ctx, null);
      fail("An exception expected");
    }
    catch (IllegalArgumentException ex) {
    }
  }

  public void testCallTemplateWithoutSourceDoc() throws FileNotFoundException, TransformerException {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/fake.xsl");
    try {
      runner.callTemplate(ctx, "aname");
      fail("An exception expected");
    }
    catch (IllegalStateException ex) {
    }
  }

  public void testApplyTemplateWithoutSourceDoc() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/fake.xsl");
    try {
      runner.applyTemplates(ctx);
      fail("An exception expected");
    }
    catch (IllegalStateException ex) {
    }

    try {
      runner.applyTemplates(ctx, xpath("aselect"));
      fail("An exception expected");
    }
    catch (IllegalStateException ex) {
    }

    try {
      runner.applyTemplates(ctx, xpath("aselect"), "amode");
      fail("An exception expected");
    }
    catch (IllegalStateException ex) {
    }
  }


  public void testCallNamedTemplate() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/name-tpl.xsl");
    ctx.setDocument("<source/>");
    Node result = runner.callTemplate(ctx, "getText");
    assertNotNull(result);

    assertEquals("atext", xpath("root/text()").toString(result));
  }

  public void testCallNamedTemplateWithGlobalParams() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/name-tpl.xsl");
    ctx.setDocument("<source/>");

    Node result = runner.callTemplate(ctx, "getGlobalParamValue");
    assertNotNull(result);

    assertEquals("", xpath("root").toString(result));

    ctx.setGlobalParamValue("aparam", "avalue");
    result = runner.callTemplate(ctx, "getGlobalParamValue");
    assertNotNull(result);

    assertEquals("avalue", xpath("root").toString(result));
  }

  public void testCallNamedTemplateWithInvokeParam() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/name-tpl.xsl");
    ctx.setDocument("<source/>");

    ctx.setTemplateParamValue("invparam1", "1");
    ctx.setTemplateParamValue("invparam2", "2");

    Node result = runner.callTemplate(ctx, "getConcatenatedInvokeParamValues");
    assertNotNull(result);

    assertEquals("1:2", xpath("root").toString(result));

    result = runner.callTemplate(ctx, "getSumOfInvokeParamValues");
    assertNotNull(result);

    assertEquals(3, xpath("root").toInt(result));
  }

  public void testGlobalVariablesDefaultValues() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/variables.xsl");
    ctx.setDocument("<source/>");

    Node result = runner.callTemplate(ctx, "getVarWithStringValue");
    assertNotNull(result);

    assertEquals("defaultvalue", xpath("root").toString(result));

    result = runner.callTemplate(ctx, "getVarWithSelectValue");
    assertNotNull(result);

    assertNotNull(xpath("*[1][self::source]").toNode(result));
    assertEquals(1, xpath("count(*)").toInt(result));

    result = runner.callTemplate(ctx, "getVarWithContentValue");
    assertNotNull(result);

    assertNotNull(xpath("*[1][self::rootElem]").toNode(result));
    assertEquals(1, xpath("count(*)").toInt(result));
  }

  public void testGlobalVariablesRedefined() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/variables.xsl");
    ctx.setDocument("<source><subElem/></source>");

    ctx.setGlobalVariableValue("varWithString", (String) null);
    Node result = runner.callTemplate(ctx, "getVarWithStringValue");
    assertNotNull(result);

    assertEquals("", xpath("text()").toString(result));

    ctx.setGlobalVariableValue("varWithString", "new value");
    result = runner.callTemplate(ctx, "getVarWithStringValue");
    assertNotNull(result);

    assertEquals("new value", xpath("root").toString(result));

    ctx.setGlobalVariableValue("varWithSelect", xpath("//subElem"));
    result = runner.callTemplate(ctx, "getVarWithSelectValue");
    assertNotNull(result);

    assertNotNull(xpath("*[1][self::subElem]").toNode(result));
    assertEquals(1, xpath("count(*)").toInt(result));

    ctx.setGlobalVariableValue("varWithContent", DOMUtil.parse("<varContent/>"));
    result = runner.callTemplate(ctx, "getVarWithContentValue");
    assertNotNull(result);

    assertNotNull(xpath("*[1][self::varContent]").toNode(result));
    assertEquals(1, xpath("count(*)").toInt(result));
  }

  public void testRelativeImportWorks() throws Exception {
    if (TestUtil.isIncorrectBaseURIForImportedStylesheets()) return;
    RunnerContext ctx = runner.newRunnerContext("tests/xml/resolver/relative-import.xsl");
    verifyImported(ctx);
  }

  private void verifyImported(final RunnerContext ctx) throws Exception {
    ctx.setDocument("<source/>");

    Node result = runner.applyTemplates(ctx);
    assertNotNull(result);

    assertNotNull(xpath("root").toNode(result));
  }

  public void testRelativeIncludeWorks() throws Exception {
    if (TestUtil.isIncorrectBaseURIForImportedStylesheets()) return;
    RunnerContext ctx = runner.newRunnerContext("tests/xml/resolver/relative-include.xsl");
    verifyImported(ctx);
  }

  public void testRelativeDocumentFunctionWorks() throws Exception {
    if (TestUtil.isIncorrectBaseURIForImportedStylesheets()) return;
    RunnerContext ctx = runner.newRunnerContext("tests/xml/resolver/document-func.xsl");
    verifyDocumentLoaded(ctx);
  }

  private void verifyDocumentLoaded(final RunnerContext ctx) throws Exception {
    ctx.setDocument("<source/>");

    Node result = runner.callTemplate(ctx, "copyDoc");
    assertNotNull(result);

    assertNotNull(xpath("document").toNode(result));
  }

  public void testTextOnlyOutput() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/not-xml-output.xsl");
    ctx.setDocument("<source/>");

    Node result = runner.callTemplate(ctx, "textOnly");
    assertEquals("The result of this template is this text.", xpath("text()").toString(result).trim());
  }

  public void testMoreThanOneRootElement() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/not-xml-output.xsl");
    ctx.setDocument("<source/>");

    Node result = runner.callTemplate(ctx, "moreThanOneRoot");
    assertEquals(2, xpath("count(root)").toInt(result));
  }

  public void testMatchRootNode() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/match-root.xsl");
    ctx.setDocument("<doc/>");

    Node result = runner.applyTemplates(ctx, xpath("/"));
    XMLComparator.assertEquals("<root>matched</root>", result);
  }

  public void testFileInputDocument() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/templates.xsl");
    ctx.setDocument(new File("tests/xml/document.xml"));

    Node result = runner.callTemplate(ctx, "getRoot");
    XMLComparator.assertEquals("<result><document/></result>", result);
  }

  private Runner runner = null;
}
