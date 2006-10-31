package org.tigris.juxy.xpath;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.util.DocumentsAssertionError;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.util.XMLComparator;
import org.w3c.dom.Node;

/**
 * @author pavel
 */
public class XPathAssert {
  private XPathExpr xpath;
  private AssertionEvaluator evaluator;

  /**
   * This assertion evaluates XPath expression to boolean value and asserts that
   * result is true. This is same as {@link #XPathAssert(String, boolean)} with second argument == true.
   * @param xpathExpr XPath expression
   */
  public XPathAssert(String xpathExpr) {
    this(xpathExpr, true);
  }

  /**
   * This assertion evaluates XPath expression to integer and compares it with expected result.
   * @param xpathExpr      XPath expression
   * @param expectedResult expected result
   */
  public XPathAssert(String xpathExpr, final int expectedResult) {
    xpath = XPathFactory.newXPath(xpathExpr);
    evaluator = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        int actual = xpath.toInt(node);
        if (expectedResult != actual) {
          throw assertionError(String.valueOf(actual), String.valueOf(expectedResult));
        }
      }
    };
  }

  /**
   * This assertion evaluates XPath expression to boolean and compares it with expected result.
   * @param xpathExpr      XPath expression
   * @param expectedResult expected result
   */
  public XPathAssert(String xpathExpr, final boolean expectedResult) {
    xpath = XPathFactory.newXPath(xpathExpr);
    evaluator = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        boolean actual = xpath.toBoolean(node);
        if (expectedResult != actual) {
          throw assertionError(String.valueOf(actual), String.valueOf(expectedResult));
        }
      }
    };
  }

  /**
   * This assertion evaluates XPath expression to double and compares it with expected result with specified precision.
   * @param xpathExpr      XPath expression
   * @param expectedResult expected result
   * @param error          error value
   */
  public XPathAssert(String xpathExpr, final double expectedResult, final double error) {
    xpath = XPathFactory.newXPath(xpathExpr);
    evaluator = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        double actual = xpath.toDouble(node);
        if (Math.abs(actual - expectedResult) > error) {
          throw assertionError(String.valueOf(actual), String.valueOf(expectedResult) + "+-" + String.valueOf(error));
        }
      }
    };
  }

  /**
   * This assertion evaluates XPath expression to string and compares it with expected result.
   * No normalization is performed before comparing.
   * @param xpathExpr      XPath expression
   * @param expectedResult expected result
   */
  public XPathAssert(String xpathExpr, final String expectedResult) {
    xpath = XPathFactory.newXPath(xpathExpr);
    evaluator = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        String actual = xpath.toString(node);
        if (!expectedResult.equals(actual)) {
          throw assertionError(actual, expectedResult);
        }
      }
    };
  }

  /**
   * This assertion evaluates XPath expression to string and compares it with expected result.
   * XPath expression result can be normalized before comparing.
   * @param xpathExpr             XPath expression
   * @param expectedResult        expected result
   * @param normalizeBeforeAssert indicates whether normalization of the XPath expression result
   *                              should be performed before comparing with expected value (see {@link org.tigris.juxy.util.StringUtil#normalizeAll})
   */
  public XPathAssert(String xpathExpr, final String expectedResult, final boolean normalizeBeforeAssert) {
    xpath = XPathFactory.newXPath(xpathExpr);
    evaluator = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        String actual = xpath.toString(node);
        actual = normalizeBeforeAssert ? StringUtil.normalizeAll(actual) : actual;
        if (!expectedResult.equals(actual)) {
          throw assertionError(actual, expectedResult);
        }
      }
    };
  }

  /**
   * This assertion evaluates XPath expression to Node and compares it with expected node using {@link org.tigris.juxy.util.XMLComparator} class.
   * @param xpathExpr    XPath expression
   * @param expectedNode node to compare result with
   */
  public XPathAssert(String xpathExpr, final Node expectedNode) {
    xpath = XPathFactory.newXPath(xpathExpr);
    evaluator = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        Node actual = xpath.toNode(node);
        if (actual == null) {
          throw new AssertionError("XPath expression " + xpath.getExpression() + " returned null");
        }
        try {
          XMLComparator.assertEquals(expectedNode, actual);
        } catch (DocumentsAssertionError error) {
          throw new AssertionError(error.getMessage());
        }
      }
    };
  }

  /**
   * Registers a namespace in the xpath expression. Returns the same XPathAssert object.
   * @param prefix namespace prefix
   * @param uri    namespace URI
   * @return same XPathAssert object
   * @throws XPathExpressionException
   */
  public XPathAssert addNamespace(String prefix, String uri) throws XPathExpressionException {
    xpath.addNamespace(prefix, uri);
    return this;
  }

  /**
   * Evaluates this assertion.
   * @param node node to evaluate assertion on
   * @throws XPathExpressionException
   * @throws AssertionError if assertion failed
   */
  public void eval(Node node) throws XPathExpressionException, AssertionError {
    ArgumentAssert.notNull(node, "Node must not be null");
    evaluator.eval(node);
  }

  private AssertionError assertionError(final String actual, final String expectedResult) {
    return new AssertionError(xpath.getExpression() + " expected <" + expectedResult + "> but was <" + actual + ">");
  }

  private interface AssertionEvaluator {
    void eval(Node node) throws XPathExpressionException, AssertionError;
  }
}
