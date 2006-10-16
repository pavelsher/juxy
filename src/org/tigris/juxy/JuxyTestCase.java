package org.tigris.juxy;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.tigris.juxy.util.*;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathFactory;
import org.tigris.juxy.xpath.XPathAssert;
import org.tigris.juxy.validator.ValidationFailedException;
import org.tigris.juxy.validator.ValidatorFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.URIResolver;
import java.io.ByteArrayOutputStream;

/**
 * <p/>
 * @author Pavel Sher
 */
public abstract class JuxyTestCase extends TestCase {
  private Runner runner;
  private RunnerContext context;

  /**
   * Creates a new RunnerContext object. RunnerContext holds all information required
   * for calling / applying templates.
   * @param systemId system id of the stylesheet (path to a stylesheet file)
   * @return new RunnerContext object
   */
  public RunnerContext newContext(String systemId) {
      context = getRunner().newRunnerContext(systemId);
      return context;
  }

  /**
   * Creates a new RunnerContext object. RunnerContext holds all information required
   * for calling / applying templates.
   * @param systemId system id of the stylesheet
   * @param resolver URIResolver to use for URI resolution during transformation
   * @return new RunnerContext object
   */
  public RunnerContext newContext(String systemId, URIResolver resolver) {
      context = getRunner().newRunnerContext(systemId, resolver);
      return context;
  }

  /**
   * Returns current RunnerContext object.
   * @return current RunnerContext object
   */
  public RunnerContext context() {
      if (context == null)
          throw new IllegalStateException("Call newContext() method first");
      return context;
  }

  /**
   * Sets RunnerContext object to use as the current context.
   * @param context
   */
  public void setContext(RunnerContext context) {
      ArgumentAssert.notNull(context, "Context must not be null");
      this.context = context;
  }

  /**
   * Creates new XPathExpr object.
   * @param xpathExpr an XPath expression
   * @return new XPathExpr object
   * @throws Exception
   */
  public XPathExpr xpath(String xpathExpr) throws Exception {
      return XPathFactory.newXPath(xpathExpr);
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext)}
   */
  public Node applyTemplates() throws Exception {
      return getRunner().applyTemplates(getContext());
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext, org.tigris.juxy.xpath.XPathExpr)}
   */
  public Node applyTemplates(XPathExpr xpath) throws Exception {
      return getRunner().applyTemplates(getContext(), xpath);
  }

  /**
   * See {@link Runner#applyTemplates(RunnerContext, org.tigris.juxy.xpath.XPathExpr, String)}
   */
  public Node applyTemplates(XPathExpr xpath, String mode) throws Exception {
      return getRunner().applyTemplates(getContext(), xpath, mode);
  }

  /**
   * See {@link Runner#callTemplate(RunnerContext, String)}
   */
  public Node callTemplate(String name) throws Exception {
      return getRunner().callTemplate(getContext(), name);
  }

  /**
   * Asserts two documents are equal. Meaningless spaces will be ignored during this assertion.
   * @param expected XML document which is expected
   * @param actual document root node of actual transformation result
   * @throws Exception
   */
  public static void assertXMLEquals(Node expected, Node actual) throws Exception {
    try {
      XMLComparator.assertEquals(expected, actual);
    } catch (DocumentsAssertionError error) {
      throw new AssertionFailedError(error.getMessage());
    }
  }

  /**
   * Asserts two documents are equal. Meaningless spaces will be ignored during this assertion.
   * @param expectedDocument XML document which is expected
   * @param actual document root node of actual transformation result
   * @throws Exception
   */
  public static void assertXMLEquals(String expectedDocument, Node actual) throws Exception {
    try {
      XMLComparator.assertEquals(expectedDocument, actual);
    } catch (DocumentsAssertionError error) {
      throw new AssertionFailedError(error.getMessage());
    }
  }

  /**
   * Asserts two documents are equal. Meaningless spaces will be ignored during this assertion.
   * @param expectedDocument XML document which is expected
   * @param actualDocument actual xml document
   * @throws Exception
   */
  public static void assertXMLEquals(String expectedDocument, String actualDocument) throws Exception {
    try {
      XMLComparator.assertEquals(expectedDocument, actualDocument);
    } catch (DocumentsAssertionError error) {
      throw new AssertionFailedError(error.getMessage());
    }
  }

  /**
   * See {@link StringUtil#normalizeSpaces(String)}
   * @param str string to normalize
   * @return normalized string
   */
  public String normalizeSpaces(String str) {
      return StringUtil.normalizeSpaces(str);
  }

  /**
   * See {@link StringUtil#normalizeAll(String)}
   * @param str string to normalize
   * @return normalized string
   */
  public String normalizeAll(String str) {
      return StringUtil.normalizeAll(str);
  }

  /**
   * Prints fragment of the document to System.out starting from the specified node.
   * @param node node to display
   * @throws Exception
   */
  public void print(Node node) throws Exception {
      ArgumentAssert.notNull(node, "Node must not be null");
      System.out.println(asString(node));
  }

  /**
   * Serializes fragment of the document to String, starting from the specified node.
   * @param node node to display
   * @return xml document corresponding to the specified node
   * @throws Exception
   */
  public String asString(Node node) throws Exception {
      ArgumentAssert.notNull(node, "Node must not be null");
      ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
      DOMUtil.printDOM(node, bos);
      return bos.toString();
  }

  /**
   * Parses specified string into org.w3c.dom.Document.
   * @param document xml document
   * @return DOM Document
   * @throws Exception
   */
  public Document parse(String document) throws Exception {
      ArgumentAssert.notEmpty(document, "Document must not be empty");
      return DOMUtil.parse(document);
  }

  /**
   * See {@link Runner#enableTracing()}.
   */
  public void enableTracing() {
      getRunner().enableTracing();
  }

  /**
   * See {@link Runner#disableTracing()}.
   */
  public void disableTracing() {
      getRunner().disableTracing();
  }

 /**
  * Validates specified node using XML schema with specified path.
  * @param node node to validate
  * @param pathToSchema path to W3C XML schema
  * @throws ValidationFailedException if validation failed
  */
  public void validateWithSchema(Node node, String pathToSchema)
      throws ValidationFailedException 
  {
    ValidatorFactory.createXMLSchemaValidator(pathToSchema).validate(node);
  }

  /**
   * Validates specified node using a number of provided XPath assertions.
   * During validation all assertions will be evaluated even if it is already known
   * that document is invalid.
   * @param node node to validate
   * @param assertions XPath assertions to evaluate against specified node
   * @throws ValidationFailedException if validation failed (i.e. if at least one assertion failed)
   */
    public void validateWithXPath(Node node, XPathAssert[] assertions)
        throws ValidationFailedException
    {
      ValidatorFactory.createXPathValidator(assertions).validate(node);
    }

  /**
   * See {@link #validateWithXPath(org.w3c.dom.Node, org.tigris.juxy.xpath.XPathAssert[])}
   */
    public void validateWithXPath(Node node, XPathAssert assertion1, XPathAssert assertion2)
        throws ValidationFailedException
    {
      validateWithXPath(node, new XPathAssert[] {assertion1, assertion2});
    }

  /**
   * See {@link #validateWithXPath(org.w3c.dom.Node, org.tigris.juxy.xpath.XPathAssert[])}
   */
    public void validateWithXPath(Node node, XPathAssert assertion1, XPathAssert assertion2, XPathAssert assertion3)
        throws ValidationFailedException
    {
      validateWithXPath(node, new XPathAssert[] {assertion1, assertion2, assertion3});
    }

  /**
   * Creates new XPath assertion
   * @param xpathExpr XPath expression
   * @param expectedResult expected result of the XPath expression
   * @return
   */
    public XPathAssert xpathAssert(String xpathExpr, int expectedResult) {
      return new XPathAssert(xpathExpr, expectedResult);
    }

  /**
   * See {@link #xpathAssert(String, int)}
   */
    public XPathAssert xpathAssert(String xpathExpr, boolean expectedResult) {
      return new XPathAssert(xpathExpr, expectedResult);
    }

  /**
   * See {@link #xpathAssert(String, int)}
   */
    public XPathAssert xpathAssert(String xpathExpr, String expectedResult) {
      return new XPathAssert(xpathExpr, expectedResult);
    }

  /**
   * See {@link #xpathAssert(String, int)}
   */
    public XPathAssert xpathAssert(String xpathExpr, double expectedResult, double error) {
      return new XPathAssert(xpathExpr, expectedResult, error);
    }

    private RunnerContext getContext() {
        if (context == null)
            throw new IllegalStateException("Call newContext method first");
        return context;
    }

    private Runner getRunner() {
        if (runner == null)
            runner = RunnerFactory.newRunner();
        return runner;
    }
}
