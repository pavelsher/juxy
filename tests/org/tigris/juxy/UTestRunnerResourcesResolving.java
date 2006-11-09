package org.tigris.juxy;

import org.w3c.dom.Node;
import junit.framework.TestSuite;

public class UTestRunnerResourcesResolving extends JuxyTestCase {
  public static TestSuite suite() {
    if (!TestUtil.isCustomURIResolverSupported() || TestUtil.isIncorrectBaseURIForImportedStylesheets()) {
      return new TestSuite();
    }

    return new TestSuite(UTestRunnerResourcesResolving.class);
  }

  public void testResourceResolutionFromJar() throws Exception {
    newContext("/xml/resolver/resource-import.xsl");
    verifyImported();

    // for some reason Xalan does not call URI resolver
    // when it loads xml document from XSLT function document()
    if (TestUtil.isURIResolverUsedByDocumentFunction()) {
      verifyDocumentLoaded();
    }
  }

  public void testResourceResolutionFromClasses() throws Exception {
    newContext("/xml/resolver/relative-import.xsl");
    verifyImported();
  }

  private void verifyImported() throws Exception {
    context().setDocument("<source/>");
    Node result = applyTemplates();
    xpathAssert("count(/root)", 1).eval(result);
  }

  private void verifyDocumentLoaded() throws Exception {
    context().setDocument("<source/>");
    Node result = callTemplate("copyDoc");
    xpathAssert("count(/document)", 2).eval(result);
  }
}
