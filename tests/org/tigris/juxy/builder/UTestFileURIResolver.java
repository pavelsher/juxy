package org.tigris.juxy.builder;

import junit.framework.TestCase;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.net.URI;

/**
 */
public class UTestFileURIResolver extends TestCase {
    private FileURIResolver resolver;

    protected void setUp() throws Exception {
        resolver = new FileURIResolver();
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
}
