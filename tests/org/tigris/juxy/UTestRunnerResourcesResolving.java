package org.tigris.juxy;

import org.w3c.dom.Node;

/**
 * User: pavel
 * Date: 25.10.2006
 */
public class UTestRunnerResourcesResolving extends JuxyTestCase {
  public void testResourceResolutionFromJar() throws Exception {
    newContext("/xml/resolver/resource-import.xsl");
    verifyImported();
    verifyDocumentLoaded();
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
