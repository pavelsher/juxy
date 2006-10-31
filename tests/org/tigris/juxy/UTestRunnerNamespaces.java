package org.tigris.juxy;

import org.w3c.dom.Node;

public class UTestRunnerNamespaces extends JuxyTestCase {
  private Runner runner = null;

  public void setUp() {
    runner = RunnerFactory.newRunner();
  }

  public void testGlobalVariables() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/variable.xsl");
    ctx.setDocument("<source/>");
    ctx.registerNamespace("ns1", "http://ns1.net");
    ctx.setGlobalVariableValue("ns1:var", "avalue");

    Node result = runner.callTemplate(ctx, "getter");
    assertNotNull(result);

    assertEquals("avalue", xpath("root").toString(result));
  }

  public void testGlobalParams() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/param.xsl");
    ctx.setDocument("<source/>");
    ctx.registerNamespace("ns1", "http://ns1.net");
    ctx.setGlobalParamValue("ns1:par", "avalue");

    Node result = runner.callTemplate(ctx, "getter");
    assertNotNull(result);

    assertEquals("avalue", xpath("root").toString(result));
  }

  public void testInvokeParams() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/invoke.xsl");
    ctx.setDocument("<source/>");
    ctx.registerNamespace("ns1", "http://ns1.net");
    ctx.setTemplateParamValue("ns1:par", "avalue");

    Node result = runner.callTemplate(ctx, "getter");
    assertNotNull(result);

    assertEquals("avalue", xpath("root").toString(result));
  }

  public void testTemplates() throws Exception {
    RunnerContext ctx = runner.newRunnerContext("tests/xml/namespaces/templates.xsl");
    ctx.setDocument("<source/>");
    ctx.registerNamespace("ns1", "http://ns1.net");

    Node result = runner.callTemplate(ctx, "ns1:named");
    assertNotNull(result);
    assertNotNull(xpath("named").toString(result));

    result = runner.applyTemplates(ctx, xpath("/"), "ns1:mode");
    assertNotNull(result);
    assertNotNull(xpath("matched").toString(result));
  }
}
