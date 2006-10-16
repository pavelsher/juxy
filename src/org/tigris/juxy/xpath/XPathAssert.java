package org.tigris.juxy.xpath;

import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.tigris.juxy.xpath.XPathFactory;
import org.w3c.dom.Node;

/**
 * User: pavel
 * Date: 14.10.2006
 */
public class XPathAssert {
  private XPathExpr xpath;
  private AssertionEvaluator assertion;

  public XPathAssert(String xpathExpr, final int expectedResult) {
    xpath = XPathFactory.newXPath(xpathExpr);
    assertion = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        int actual = xpath.toInt(node);
        if (expectedResult != actual) {
          throw assertionError(String.valueOf(actual), String.valueOf(expectedResult));
        }
      }
    };
  }

  public XPathAssert(String xpathExpr, final boolean expectedResult) {
    xpath = XPathFactory.newXPath(xpathExpr);
    assertion = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        boolean actual = xpath.toBoolean(node);
        if (expectedResult != actual) {
          throw assertionError(String.valueOf(actual), String.valueOf(expectedResult));
        }
      }
    };
  }

  public XPathAssert(String xpathExpr, final double expectedResult, final double error) {
    xpath = XPathFactory.newXPath(xpathExpr);
    assertion = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        double actual = xpath.toDouble(node);
        if (Math.abs(actual - expectedResult) > error) {
          throw assertionError(String.valueOf(actual), String.valueOf(expectedResult) + "+-" + String.valueOf(error));
        }
      }
    };
  }

  public XPathAssert(String xpathExpr, final String expectedResult) {
    xpath = XPathFactory.newXPath(xpathExpr);
    assertion = new AssertionEvaluator() {
      public void eval(Node node) throws XPathExpressionException {
        String actual = xpath.toString(node);
        if (!expectedResult.equals(actual)) {
          throw assertionError(actual, expectedResult);
        }
      }
    };
  }

  public void eval(Node node) throws XPathExpressionException, AssertionError {
    assertion.eval(node);
  }

  private AssertionError assertionError(final String actual, final String expectedResult) {
    return new AssertionError(xpath.getExpression() + " expected <" + expectedResult + "> but was <" + actual + ">");
  }

  private interface AssertionEvaluator {
    void eval(Node node) throws XPathExpressionException, AssertionError;
  }
}
