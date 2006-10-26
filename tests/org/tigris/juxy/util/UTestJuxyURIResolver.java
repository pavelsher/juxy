package org.tigris.juxy.util;

import junit.framework.TestCase;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 */
public class UTestJuxyURIResolver extends TestCase {
    private JuxyURIResolver resolver;

    protected void setUp() throws Exception {
        resolver = new JuxyURIResolver();
    }

    public void testResolveNonExistentFile() throws TransformerException {
        assertNull(resolver.resolve(null, null));
        assertNull(resolver.resolve("unknown_file.xml", null));
    }

    public void testRelativeResolveToExistingFile() throws Exception {
        Source src = resolver.resolve("tests/xml/document.xml", null);
        assertNotNull(src);
        assertEquals(new File("tests/xml/document.xml").toURI(), new URI(src.getSystemId()));
    }

    public void testRelativeResolveWithNotEmptyBase() throws Exception {
        Source src = resolver.resolve("tests/xml/document.xml", System.getProperty("user.dir"));
        assertNotNull(src);
        assertEquals(new File("tests/xml/document.xml").toURI(), new URI(src.getSystemId()));
    }

    public void testAbsoluteResolveWithNotEmptyBase() throws Exception {
        Source src = resolver.resolve(new File("tests/xml/document.xml").getAbsolutePath(), System.getProperty("user.dir"));
        assertNotNull(src);
        assertEquals(new File("tests/xml/document.xml").toURI(), new URI(src.getSystemId()));
    }

    public void testAbsoluteResolveWithEmptyBase() throws Exception {
        Source src = resolver.resolve(new File("tests/xml/document.xml").getAbsoluteFile().toURI().toString(), "");
        assertNotNull(src);
        assertEquals(new File("tests/xml/document.xml").toURI(), new URI(src.getSystemId()));
    }

    public void testResourceResolving_NotWithinJar() throws TransformerException, URISyntaxException {
      String expectedURI = getClass().getResource("/xml/resolver/file.xml").toString();

      Source src = resolver.resolve("/xml/resolver/file.xml", null);
      assertNotNull(src);
      assertEquals(expectedURI, src.getSystemId());

      src = resolver.resolve("file.xml", "/xml/resolver/file.xsl");
      assertNotNull(src);
      assertEquals(expectedURI, src.getSystemId());

      src = resolver.resolve("../file.xml", "/xml/resolver/path/file.xsl");
      assertNotNull(src);
      assertEquals(expectedURI, src.getSystemId());
    }

    public void testResourceResolving_WithinJar() throws TransformerException, URISyntaxException {
      String expectedURI = getClass().getResource("/xml/document.xml").toString();

      Source src = resolver.resolve("/xml/document.xml", null);
      assertNotNull(src);
      assertEquals(expectedURI, src.getSystemId());

      src = resolver.resolve("resource-import.xsl", getClass().getResource("/xml/resolver/resource-import.xsl").toString());
      assertNotNull(src);
      assertEquals(getClass().getResource("/xml/resolver/resource-import.xsl").toString(), src.getSystemId());

      src = resolver.resolve("../document.xml", getClass().getResource("/xml/resolver/resource-import.xsl").toString());
      assertNotNull(src);
      assertEquals(expectedURI, src.getSystemId());

      src = resolver.resolve(expectedURI, getClass().getResource("/xml/resolver/resource-import.xsl").toString());
      assertNotNull(src);
      assertEquals(expectedURI, src.getSystemId());
    }
}
