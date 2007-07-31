import org.tigris.juxy.JuxyTestCase;
import org.tigris.juxy.util.DOMUtil;
import org.w3c.dom.Node;

/**
 * This sample demostrates how to work with parameters and variables in Juxy.
 */
public class ParametersTestCase extends JuxyTestCase {
  protected void setUp() throws Exception {
    newContext("samples/xsl/parameters.xsl");
  }

  public void testTemplateParameter_SimpleValue() throws Exception {
    context().setDocument("<root/>");
    context().setTemplateParamValue("param1", "value1");
    Node result = callTemplate("template-parameter");
    print(result);
    assertEquals("value1", DOMUtil.innerText(result));
  }

  public void testTemplateParameter_DocumentValue() throws Exception {
    context().setDocument("<root/>");
    context().setTemplateParamValue("param1", DOMUtil.parse("<doc>some text</doc>"));
    Node result = callTemplate("template-parameter");
    print(result);
    assertEquals("some text", DOMUtil.innerText(result));
  }

  public void testTemplateParameter_XPathValue() throws Exception {
    context().setDocument("" +
        "<root>" +
        "<node1>node1</node1>" +
        "<node2>node2</node2>" +
        "</root>");
    context().setTemplateParamValue("param1", xpath("/root/node2"));
    Node result = callTemplate("template-parameter");
    print(result);
    assertEquals("node2", DOMUtil.innerText(result));
  }

  public void testGlobalVariableRedefinition() throws Exception {
    context().setDocument("<root/>");
    context().setGlobalVariableValue("globalVar", "another value");
    Node result = callTemplate("template-global-variable");
    print(result);
    assertEquals("another value", DOMUtil.innerText(result));
  }

  public void testGlobalParameterRedefinition() throws Exception {
    context().setDocument("<root/>");
    context().setGlobalParamValue("globalParam", "some value");
    Node result = callTemplate("template-global-parameter");
    print(result);
    assertEquals("some value", DOMUtil.innerText(result));
  }
}
