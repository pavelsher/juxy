import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for namespace.xsl.
 * namespace.xsl converts nodes from one namespace to another.
 */
public class NamespaceTestCase extends JuxyTestCase {
  protected void setUp() throws Exception {
    newContext("samples/xsl/namespace.xsl");
    context().registerNamespace("oldns", "http://juxy.tigris.org/0.9");
    context().registerNamespace("newns", "http://juxy.tigris.org/1.0");
  }

  public void testElementsConverted() throws Exception {
    context().setDocument("" +
        "<element xmlns='http://juxy.tigris.org/0.9' attribute='value'>" +
        "</element>");
    Node result = applyTemplates();
    assertXMLEquals(
        "<element xmlns='http://juxy.tigris.org/1.0' attribute='value'/>",
        result
    );
  }

  public void testTextAndOtherNodesAreCopied() throws Exception {
    context().setDocument("" +
        "<element xmlns='http://juxy.tigris.org/0.9'>" +
        "text" +
        "<!-- comment -->" +
        "<child/>" +
        "</element>");
    Node result = applyTemplates();
    assertXMLEquals(
        "<element xmlns='http://juxy.tigris.org/1.0'>" +
            "text" +
            "<!-- comment -->" +
            "<child/>" +
            "</element>",
        result
    );
  }
}
