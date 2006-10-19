package org.tigris.juxy.xpath;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.util.StringUtil;
import org.w3c.dom.Node;

/**
 * User: pavel
 * Date: 14.10.2006
 */
public class XPathAssert {
  private XPathExpr xpath;
  private AssertionEvaluator evaluator;

    /**
     * Constructs assertion from the XPath expression with specified expected result.
     * @param xpathExpr XPath expression
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
     * Constructs assertion from the XPath expression with specified expected result.
     * @param xpathExpr XPath expression
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
     * Constructs assertion from the XPath expression with specified expected result.
     * @param xpathExpr XPath expression
     * @param expectedResult expected result
     * @param error error value
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
     * Constructs assertion from the XPath expression with specified expected result.
     * @param xpathExpr XPath expression
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
     * Constructs assertion from the XPath expression with specified expected result.
     * @param xpathExpr XPath expression
     * @param expectedResult expected result
     * @param normalizeBeforeAssert indicates whether normalization of the XPath expression result
     * should be performed before comparing with expected value (see {@link org.tigris.juxy.util.StringUtil#normalizeAll})
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
