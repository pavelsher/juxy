package org.tigris.juxy;

import junit.framework.TestCase;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.validator.ValidationFailedException;
import org.tigris.juxy.xpath.XPathAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.URIResolver;

/**
 * Base class for JUnit test cases.
 *
 * @author Pavel Sher
 * @noinspection JavaDoc
 */
public abstract class JuxyTestCase extends TestCase {
  private JuxyAdapter delegate = new JuxyAdapter();

  protected JuxyTestCase() {
  }

  protected JuxyTestCase(String name) {
    super(name);
  }

  /**
   * See {@link JuxyAdapter#newContext(String)}
   */
  public RunnerContext newContext(String systemId) {
    return delegate.newContext(systemId);
  }

  /**
   * See {@link JuxyAdapter#newContext(String, URIResolver)}
   */
  public RunnerContext newContext(String systemId, URIResolver resolver) {
    return delegate.newContext(systemId, resolver);
  }

  /**
   * See {@link JuxyAdapter#context()}
   */
  public RunnerContext context() {
    return delegate.context();
  }

  /**
   * See {@link JuxyAdapter#setContext(RunnerContext)}
   */
  public void setContext(RunnerContext context) {
    delegate.setContext(context);
  }

  /**
   * See {@link JuxyAdapter#xpath(String)}
   */
  public XPathExpr xpath(String xpathExpr) throws Exception {
    return delegate.xpath(xpathExpr);
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext)}
   */
  public Node applyTemplates() throws Exception {
    return delegate.applyTemplates();
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext,org.tigris.juxy.xpath.XPathExpr)}
   */
  public Node applyTemplates(XPathExpr xpath) throws Exception {
    return delegate.applyTemplates(xpath);
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext,org.tigris.juxy.xpath.XPathExpr,String)}
   */
  public Node applyTemplates(XPathExpr xpath, String mode) throws Exception {
    return delegate.applyTemplates(xpath, mode);
  }

  /**
   * See {@link Runner#callTemplate(RunnerContext,String)}
   */
  public Node callTemplate(String name) throws Exception {
    return delegate.callTemplate(name);
  }

  /**
   * See {@link JuxyAdapter#assertXMLEquals(Node, Node)}
   */
  public void assertXMLEquals(Node expected, Node actual) throws Exception {
    delegate.assertXMLEquals(expected, actual);
  }

  /**
   * See {@link JuxyAdapter#assertXMLEquals(String, Node)}
   */
  public void assertXMLEquals(String expectedDocument, Node actual) throws Exception {
    delegate.assertXMLEquals(expectedDocument, actual);
  }

  /**
   * See {@link JuxyAdapter#assertXMLEquals(String, String)}
   */
  public void assertXMLEquals(String expectedDocument, String actualDocument) throws Exception {
    delegate.assertXMLEquals(expectedDocument, actualDocument);
  }

  /**
   * See {@link StringUtil#normalizeSpaces(String)}
   */
  public String normalizeSpaces(String str) {
    return delegate.normalizeSpaces(str);
  }

  /**
   * See {@link StringUtil#normalizeAll(String)}
   */
  public String normalizeAll(String str) {
    return delegate.normalizeAll(str);
  }

  /**
   * See {@link JuxyAdapter#print(Node)}
   */
  public void print(Node node) throws Exception {
    delegate.print(node);
  }

  /**
   * See {@link JuxyAdapter#asString(Node)}
   */
  public String asString(Node node) throws Exception {
    return delegate.asString(node);
  }

  /**
   * See {@link JuxyAdapter#parse(String)}
   */
  public Document parse(String document) throws Exception {
    return delegate.parse(document);
  }

  /**
   * See {@link Runner#enableTracing()}.
   */
  public void enableTracing() {
    delegate.enableTracing();
  }

  /**
   * See {@link Runner#disableTracing()}.
   */
  public void disableTracing() {
    delegate.disableTracing();
  }

  /**
   * See {@link JuxyAdapter#validateWithSchema(Node, String)}
   */
  public void validateWithSchema(Node node, String systemId) throws ValidationFailedException {
    delegate.validateWithSchema(node, systemId);
  }

  /**
   * See {@link JuxyAdapter#evalAssertions(Node, XPathAssert[])}
   */
  public void evalAssertions(Node node, XPathAssert[] assertions) throws XPathExpressionException, AssertionError {
    delegate.evalAssertions(node, assertions);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String)}
   */
  public XPathAssert xpathAssert(String xpathExpr) {
    return delegate.xpathAssert(xpathExpr);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,int)}
   */
  public XPathAssert xpathAssert(String xpathExpr, int expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,boolean)}
   */
  public XPathAssert xpathAssert(String xpathExpr, boolean expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,String)}
   */
  public XPathAssert xpathAssert(String xpathExpr, String expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,String,boolean)}
   */
  public XPathAssert xpathAssert(String xpathExpr, String expectedResult, boolean normalize) {
    return delegate.xpathAssert(xpathExpr, expectedResult, normalize);
  }

  /**
   * See {@link org.tigris.juxy.xpath.XPathAssert#XPathAssert(String,double,double)}
   */
  public XPathAssert xpathAssert(String xpathExpr, double expectedResult, double error) {
    return delegate.xpathAssert(xpathExpr, expectedResult, error);
  }

  /**
   * See {@link XPathAssert#XPathAssert(String,Node)}
   */
  public XPathAssert xpathAssert(String xpathExpr, Node expectedResult) {
    return delegate.xpathAssert(xpathExpr, expectedResult);
  }
}
