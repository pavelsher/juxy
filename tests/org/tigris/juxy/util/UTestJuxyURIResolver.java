package org.tigris.juxy.util;

import junit.framework.TestCase;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 */
public class UTestJuxyURIResolver extends TestCase {
  private JuxyURIResolver resolver;

  protected void setUp() throws Exception {
    resolver = new JuxyURIResolver();
  }

  private URI fileToURI(String path) throws IOException {
    return new File(path).getCanonicalFile().toURI();
  }

  public void testResolveNonExistentFile() throws TransformerException {
    assertNull(resolver.resolve(null, null));
    assertNull(resolver.resolve("unknown_file.xml", null));
  }

  public void testRelativeResolveToExistingFile() throws Exception {
    Source src = resolver.resolve("tests/xml/document.xml", null);
    assertNotNull(src);
    assertEquals(fileToURI("tests/xml/document.xml"), new URI(src.getSystemId()));
  }

  public void testRelativeResolveWithNotEmptyBase() throws Exception {
    Source src = resolver.resolve("tests/xml/document.xml", System.getProperty("user.dir"));
    assertNotNull(src);
    assertEquals(fileToURI("tests/xml/document.xml"), new URI(src.getSystemId()));
  }

  public void testAbsoluteResolveWithNotEmptyBase() throws Exception {
    Source src = resolver.resolve(new File("tests/xml/document.xml").getAbsolutePath(), System.getProperty("user.dir"));
    assertNotNull(src);
    assertEquals(fileToURI("tests/xml/document.xml"), new URI(src.getSystemId()));
  }

  public void testAbsoluteResolveWithEmptyBase() throws Exception {
    Source src = resolver.resolve(new File("tests/xml/document.xml").getAbsoluteFile().toURI().toString(), "");
    assertNotNull(src);
    assertEquals(fileToURI("tests/xml/document.xml"), new URI(src.getSystemId()));
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

    String expectedXsltURI = getClass().getResource("/xml/resolver/resource-import.xsl").toString();

    src = resolver.resolve("resource-import.xsl", expectedXsltURI);
    assertNotNull(src);
    assertEquals(expectedXsltURI, src.getSystemId());

    src = resolver.resolve("../document.xml", expectedXsltURI);
    assertNotNull(src);
    assertEquals(expectedURI, src.getSystemId());

    src = resolver.resolve(expectedURI, expectedXsltURI);
    assertNotNull(src);
    assertEquals(expectedURI, src.getSystemId());
  }

  public void testMoreThanOneResourceWithSameName() throws TransformerException, URISyntaxException, IOException {
    List resources = new ArrayList();

    Enumeration resourcesEnum = getClass().getClassLoader().getResources("xml/document.xml");
    while (resourcesEnum.hasMoreElements()) {
      resources.add(resourcesEnum.nextElement());
    }
    assertEquals(3, resources.size());
    assertTrue(resources.get(0).toString().contains("resources.jar"));
    assertTrue(resources.get(1).toString().contains("test-classes"));
    assertTrue(resources.get(2).toString().contains("resources2.jar"));

    Enumeration expected = getClass().getClassLoader().getResources("xml/imported.xsl");
    String expectedURLInFirstJar = expected.nextElement().toString();
    String expectedURLInClasses = expected.nextElement().toString();
    String expectedURLInSecondJar = expected.nextElement().toString();

    Source src = resolver.resolve("imported.xsl", resources.get(0).toString());
    assertNotNull(src);
    assertEquals(expectedURLInFirstJar, src.getSystemId());

    src = resolver.resolve("imported.xsl", resources.get(1).toString());
    assertNotNull(src);
    assertEquals(expectedURLInClasses, src.getSystemId());

    src = resolver.resolve("imported.xsl", resources.get(2).toString());
    assertNotNull(src);
    assertEquals(expectedURLInSecondJar, src.getSystemId());
  }
}
