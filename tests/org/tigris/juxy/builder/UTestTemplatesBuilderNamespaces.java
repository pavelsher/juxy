package org.tigris.juxy.builder;

import org.tigris.juxy.GlobalVariable;
import org.tigris.juxy.util.XMLComparator;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;

public class UTestTemplatesBuilderNamespaces extends BaseTestTemplatesBuilder {
  public void testTemplatesIsSameNamespacesEmpty() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setNamespaces(null);
    assertSame(orig, builder.build());

    builder.setNamespaces(Collections.EMPTY_MAP);
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsSameNamespacesNotEmpty() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Map namespaces = new HashMap();
    namespaces.put("http://ns1.net", "ns1");
    namespaces.put("http://ns2.net", "ns2");
    builder.setNamespaces(namespaces);
    Templates orig = builder.build();

    builder.setNamespaces(namespaces);
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsNotSameNamespacesEmpty() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Map namespaces = new HashMap();
    namespaces.put("http://ns1.net", "ns1");
    namespaces.put("http://ns2.net", "ns2");
    builder.setNamespaces(namespaces);
    Templates orig = builder.build();

    builder.setNamespaces(Collections.EMPTY_MAP);
    assertNotSame(orig, builder.build());
  }

  public void testTemplatesIsNotSameNamespacesNotEmpty() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Map namespacesOrig = new HashMap();
    namespacesOrig.put("http://ns1.net", "ns1");
    namespacesOrig.put("http://ns2.net", "ns2");
    builder.setNamespaces(namespacesOrig);
    Templates orig = builder.build();

    Map namespacesNew = new HashMap();
    namespacesNew.putAll(namespacesOrig);
    namespacesNew.put("http://ns3.net", "ns3");

    builder.setNamespaces(namespacesNew);
    assertNotSame(orig, builder.build());
  }

  public void testTemplatesDefaultNamespace_DOM() throws Exception {
    String testingXsltSystemId = getTestingXsltSystemId("tests/xml/fake.xsl");
    builder.setImportSystemId(testingXsltSystemId, null);
    Map namespaces = new HashMap();
    namespaces.put("http://ns1.net", "");
    builder.setNamespaces(namespaces);
    builder.build();

    XMLComparator.assertEquals(
        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' " +
            "                xmlns='http://ns1.net' " +
            "                version='1.0'>\n" +
            "   <xsl:import href='" + testingXsltSystemId + "'/>" +
            "</xsl:stylesheet>",
        builder.getCurrentStylesheetDoc()
    );
  }

  public void testNamespaces_DOM() throws MalformedURLException, XPathExpressionException, TransformerException, SAXException {
    String testingXsltSystemId = getTestingXsltSystemId("tests/xml/fake.xsl");
    builder.setImportSystemId(testingXsltSystemId, null);
    Map namespaces = new HashMap();
    namespaces.put("http://ns1.net", "ns1");
    namespaces.put("http://ns2.net", "ns2");
    namespaces.put("http://ns3.net", "ns3");
    builder.setNamespaces(namespaces);
    builder.build();

    XMLComparator.assertEquals(
        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' " +
            "                xmlns:ns1='http://ns1.net' xmlns:ns3='http://ns3.net' xmlns:ns2='http://ns2.net' " +
            "                version='1.0'>\n" +
            "   <xsl:import href='" + testingXsltSystemId + "'/>" +
            "</xsl:stylesheet>",
        builder.getCurrentStylesheetDoc()
    );
  }

  public void testMoreNamespaces_DOM() throws MalformedURLException, XPathExpressionException, TransformerException, SAXException {
    String testingXsltSystemId = getTestingXsltSystemId("tests/xml/fake.xsl");
    builder.setImportSystemId(testingXsltSystemId, null);
    Map namespaces = new HashMap();
    namespaces.put("http://ns1.net", "ns1");
    namespaces.put("http://ns2.net", "ns2");
    builder.setNamespaces(namespaces);
    builder.build();

    namespaces.put("http://ns3.net", "ns3");
    builder.setNamespaces(namespaces);
    builder.build();

    XMLComparator.assertEquals(
        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' " +
            "                xmlns:ns1='http://ns1.net' xmlns:ns3='http://ns3.net' xmlns:ns2='http://ns2.net' " +
            "                version='1.0'>\n" +
            "   <xsl:import href='" + testingXsltSystemId + "'/>" +
            "</xsl:stylesheet>",
        builder.getCurrentStylesheetDoc()
    );
  }

  public void testLessNamespaces_DOM() throws MalformedURLException, XPathExpressionException, TransformerException, SAXException {
    String testingXsltSystemId = getTestingXsltSystemId("tests/xml/fake.xsl");
    builder.setImportSystemId(testingXsltSystemId, null);
    Map namespaces = new HashMap();
    namespaces.put("http://ns1.net", "ns1");
    namespaces.put("http://ns2.net", "ns2");
    namespaces.put("http://ns3.net", "ns3");
    builder.setNamespaces(namespaces);
    builder.build();

    namespaces.remove("http://ns3.net");
    namespaces.remove("http://ns2.net");
    builder.setNamespaces(namespaces);
    builder.build();

    XMLComparator.assertEquals(
        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' " +
            "                xmlns:ns1='http://ns1.net'" +
            "                version='1.0'>\n" +
            "   <xsl:import href='" + testingXsltSystemId + "'/>" +
            "</xsl:stylesheet>",
        builder.getCurrentStylesheetDoc()
    );
  }

  public void testPrefixInGlobalVarName() throws FileNotFoundException, XPathExpressionException, MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    List vars = new ArrayList();
    vars.add(new GlobalVariable("ss:aname", "avalue"));
    builder.setGlobalVariables(vars);
    Map namespaces = new HashMap();
    namespaces.put("http://ss.net", "ss");
    builder.setNamespaces(namespaces);

    assertNotNull(builder.build());
  }
}
