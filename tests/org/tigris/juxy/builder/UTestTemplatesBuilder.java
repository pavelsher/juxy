package org.tigris.juxy.builder;

import org.tigris.juxy.GlobalVariable;
import org.tigris.juxy.InvokeParam;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UTestTemplatesBuilder extends BaseTestTemplatesBuilder {
  public void testTemplatesBuild_Failed() throws TransformerConfigurationException {
    try {
      builder.build();
      fail("An exception expected");
    }
    catch (IllegalStateException ex) {
    }
  }

  public void testTemplatesIsSame_SystemId() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();
    assertSame(orig, builder.build());

    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsSame_GlVars() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setGlobalVariables(Collections.EMPTY_SET);
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsSame_CurNode() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setCurrentNode(null);
    assertSame(orig, builder.build());

    builder.setCurrentNode(xpath("test"));
    orig = builder.build();

    builder.setCurrentNode(xpath("test"));
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsSame_NamedInvStatement() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setInvokationStatementInfo("aname", null);
    orig = builder.build();

    builder.setInvokationStatementInfo("aname", Collections.EMPTY_SET);
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsSame_AppliedInvStatement() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setInvokationStatementInfo(xpath("root"), "amode", null);
    orig = builder.build();

    builder.setInvokationStatementInfo(xpath("root"), "amode", Collections.EMPTY_SET);
    assertSame(orig, builder.build());
  }

  public void testTemplatesIsNotSame_SystemId() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/name-tpl.xsl"), null);
    assertNotSame(orig, builder.build());
  }

  public void testTemplatesIsNotSame_GlVars() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    List glvars = new ArrayList();
    glvars.add(new GlobalVariable("avar", "avalue"));
    builder.setGlobalVariables(glvars);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setGlobalVariables(null);
    assertNotSame(orig, builder.build());
  }

  public void testTemplatesIsNotSame_CurNode() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setCurrentNode(xpath("fff"));
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setCurrentNode(xpath("test"));
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setCurrentNode(null);
    assertNotSame(orig, builder.build());
  }

  public void testTemplatesIsNotSame_NamedInvStatement() throws MalformedURLException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setInvokationStatementInfo("aname", null);
    assertNotSame(orig, builder.build());

    List invparams = new ArrayList();
    invparams.add(new InvokeParam("aparam", "avalue"));

    orig = builder.build();
    builder.setInvokationStatementInfo("aname", invparams);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo("aname", Collections.EMPTY_SET);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo("newname", Collections.EMPTY_SET);
    assertNotSame(orig, builder.build());
  }

  public void testTemplatesIsNotSame_AppliedInvStatement() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    Templates orig = builder.build();

    builder.setInvokationStatementInfo(null, null, null);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo(xpath("root"), null, null);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo(xpath("root"), "amode", null);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo(xpath("anotherroot"), "amode", null);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo(xpath("anotherroot"), "anothermodemode", null);
    assertNotSame(orig, builder.build());

    List invparams = new ArrayList();
    invparams.add(new InvokeParam("aparam", "avalue"));

    orig = builder.build();
    builder.setInvokationStatementInfo(xpath("anotherroot"), "anothermodemode", invparams);
    assertNotSame(orig, builder.build());

    orig = builder.build();
    builder.setInvokationStatementInfo(xpath("anotherroot"), "anothermodemode", Collections.EMPTY_SET);
    assertNotSame(orig, builder.build());
  }

  public void testSkeleton() throws TransformerException, MalformedURLException, XPathExpressionException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    builder.build();

    assertNotNull(xpath("/xsl:stylesheet")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));
    assertNotNull(xpath("/xsl:stylesheet/xsl:import[@href = '" + getTestingXsltSystemId("tests/xml/fake.xsl") + "']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));
    assertEquals(1, xpath("count(/xsl:stylesheet/*)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testGlobalVariables() throws MalformedURLException, TransformerException, XPathExpressionException {
    List glvars = new ArrayList();

    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    glvars.add(new GlobalVariable("aname1", "avalue"));
    builder.setGlobalVariables(glvars);
    builder.build();

    assertEquals("avalue", xpath("/xsl:stylesheet/xsl:variable[@name = 'aname1']/text()")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toString(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(/xsl:stylesheet/xsl:variable)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    XPathExpr xp = xpath("//elem[position() < 2]");

    glvars.add(new GlobalVariable("aname2", xp));
    builder.setGlobalVariables(glvars);
    builder.build();

    assertNotNull(xpath("/xsl:stylesheet/xsl:variable[@name = 'aname2' and @select = '" + xp.getExpression() + "']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(2, xpath("count(/xsl:stylesheet/xsl:variable)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    assertNotNull(xpath("/xsl:stylesheet/*[1][self::xsl:import]")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertNotNull(xpath("/xsl:stylesheet/*[2][self::xsl:variable and @name = 'aname1']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertNotNull(xpath("/xsl:stylesheet/*[3][self::xsl:variable and @name = 'aname2']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));
  }

  public void testNamedInvStatement() throws TransformerException, MalformedURLException, XPathExpressionException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    builder.setInvokationStatementInfo("aname", null);
    builder.build();

    assertNotNull(xpath("/xsl:stylesheet/xsl:template[@match = '/']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(/xsl:stylesheet/xsl:template)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:call-template[@name = 'aname']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:call-template[@name = 'aname'])")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testNamedInvStatementWithCurNode() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    builder.setInvokationStatementInfo("aname", null);
    XPathExpr xp = xpath("zzz");
    builder.setCurrentNode(xp);
    builder.build();

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:for-each[@select = '" + xp.getExpression() + "']/xsl:call-template[@name = 'aname']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:for-each[@select = '" + xp.getExpression() + "'])")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:call-template[@name = 'aname'])")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testAppliedInvStatement() throws TransformerException, MalformedURLException, XPathExpressionException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    builder.setInvokationStatementInfo(null, null, null);
    builder.build();

    assertNotNull(xpath("/xsl:stylesheet/xsl:template[@match = '*']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(/xsl:stylesheet/xsl:template)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    assertNotNull(xpath("//xsl:template[@match = '*']/xsl:apply-imports[not(@*)]")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:apply-imports)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testAppliedInvStatementWithSelect() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    XPathExpr xp = xpath("aselect");
    builder.setInvokationStatementInfo(xp, null, null);
    builder.build();

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:apply-templates[@select = '" + xp.getExpression() + "']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:apply-templates)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testAppliedInvStatementWithMode() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    XPathExpr xp = xpath("aselect");
    builder.setInvokationStatementInfo(xp, "amode", null);
    builder.build();

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:apply-templates[@select = '" + xp.getExpression() + "' and @mode = 'amode']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:apply-templates)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testAppliedInvStatementWithCurNode() throws MalformedURLException, XPathExpressionException, TransformerConfigurationException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    XPathExpr xp1 = xpath("aselect");
    builder.setInvokationStatementInfo(xp1, "amode", null);

    XPathExpr xp2 = xpath("zzz");
    builder.setCurrentNode(xp2);
    builder.build();

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:for-each[@select = '" + xp2.getExpression() + "']/xsl:apply-templates[@select = '" + xp1.getExpression() + "' and @mode = 'amode']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:for-each)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:apply-templates)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }

  public void testAppliedInvStatementWithRootSelect() throws MalformedURLException, XPathExpressionException, TransformerException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);
    builder.setInvokationStatementInfo(xpath("/"), "amode", null);
    builder.build();

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:apply-templates[@select = '/' and @mode = 'amode']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(1, xpath("count(//xsl:apply-templates)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));

    builder.setInvokationStatementInfo(xpath("/"), null, null);
    builder.build();

    assertNull(xpath("//xsl:template")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    builder.setCurrentNode(xpath("/"));
    XPathExpr xp = xpath("elem");
    builder.setInvokationStatementInfo(xp, null, null);
    builder.build();

    assertNotNull(xpath("//xsl:template[@match = '/']/xsl:apply-templates[@select = '" + xp.getExpression() + "']")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertNull(xpath("//xsl:for-each")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));
  }

  public void testInvStatement_WithParams() throws TransformerException, MalformedURLException, SAXException, XPathExpressionException {
    builder.setImportSystemId(getTestingXsltSystemId("tests/xml/fake.xsl"), null);

    List params = new ArrayList();
    XPathExpr xp = xpath("//elem");
    params.add(new InvokeParam("apar1", "aval1"));
    params.add(new InvokeParam("apar2", xp));
    params.add(new InvokeParam("apar3", DOMUtil.parse("<param-content/>")));

    builder.setInvokationStatementInfo("aname", params);
    builder.build();

    assertEquals("aval1", xpath("//xsl:call-template/xsl:with-param[@name = 'apar1']/text()")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toString(builder.getCurrentStylesheetDoc()));

    assertEquals(xp.getExpression(), xpath("//xsl:call-template/xsl:with-param[@name = 'apar2']/@select")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toString(builder.getCurrentStylesheetDoc()));

    assertNotNull(xpath("//xsl:call-template/xsl:with-param[@name = 'apar3' and not(@select)]/param-content")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toNode(builder.getCurrentStylesheetDoc()));

    assertEquals(3, xpath("count(//xsl:call-template/xsl:with-param)")
        .addNamespace("xsl", XSLTKeys.XSLT_NS)
        .toInt(builder.getCurrentStylesheetDoc()));
  }
}
