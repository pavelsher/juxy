
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.resolver.CatalogManager;
import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * This test case demonstrates how to use custom URIResolver
 * in tests. We will set up CatalogResolver to resolve
 * paths to XSLT stylesheets using XML catalog.
 *
 * @author Pavel Sher
 */
public class CustomResolverTestCase extends JuxyTestCase {
    public void testSimpleTransformation() throws Exception {
        CatalogManager cm = CatalogManager.getStaticManager();
        cm.setCatalogFiles("samples/samples.catalog");

        newContext("CustomResolverTestCase", new CatalogResolver(cm));
        context().setDocument("<root/>");

        Node result = callTemplate("getResult");
        xpathAssert("text()", "The result is this text.", true).eval(result);
    }
}
