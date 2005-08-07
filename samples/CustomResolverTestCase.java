
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.resolver.CatalogManager;
import org.tigris.juxy.JuxyTestCase;
import org.w3c.dom.Node;

/**
 * $Id: CustomResolverTestCase.java,v 1.1 2005-08-07 16:43:15 pavelsher Exp $
 * <p/>
 * This test case demonstrates how we can use custom URIResolver
 * in the tests. We will use CatalogResolver to resolve
 * xsl paths against XML catalog.
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
        assertEquals("The result is this text.", xpath("text()").toString(result).trim());
    }
}
