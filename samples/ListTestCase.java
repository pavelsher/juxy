
import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * Test cases for list.xsl
 */
public class ListTestCase extends JuxyTestCase {

    protected void setUp() throws Exception {
        newContext("samples/xsl/list.xsl");
    }

    public void testEmptyList() throws Exception {
        context().setCurrentNode(xpath("/list"));
        context().setDocument("<list/>");
        Node result = callTemplate("makeList");
        assertFalse(xpath("text()").toBoolean(result));
    }

    public void testOneElementInTheList() throws Exception {
        context().setCurrentNode(xpath("/list"));
        context().setDocument("" +
                "<list>" +
                "<item>one item</item>" +
                "</list>");
        Node result = callTemplate("makeList");
        assertEquals("one item", xpath("text()").toString(result).trim());
    }

    public void testMoreThanOneElementInTheList() throws Exception {
        context().setCurrentNode(xpath("/list"));
        context().setDocument("" +
                "<list>" +
                "<item>first item</item>" +
                "<item>second item</item>" +
                "<item>third item</item>" +
                "</list>");
        Node result = callTemplate("makeList");
        assertEquals("first item, second item, third item", xpath("text()").toString(result).trim());
    }

    public void testMoreThanOneElementInTheList_ApplyTemplates() throws Exception {
        context().setDocument("" +
                "<list>" +
                "<item>first item</item>" +
                "<item>second item</item>" +
                "<item>third item</item>" +
                "</list>");
        Node result = applyTemplates(xpath("/list"));
        assertEquals("first item, second item, third item", xpath("text()").toString(result).trim());
    }
}
