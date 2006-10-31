package org.tigris.juxy.builder;

import junit.framework.TestCase;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.tigris.juxy.xpath.XPathFactory;

import javax.xml.transform.TransformerFactory;
import java.io.File;

public abstract class BaseTestTemplatesBuilder extends TestCase {
  protected TemplatesBuilderImpl builder = null;
  private static TransformerFactory trFactory = TransformerFactory.newInstance();

  public void setUp() {
    builder = new TemplatesBuilderImpl(trFactory);
  }

  protected String getTestingXsltSystemId(String filePath) {
    return new File(filePath).toURI().toString();
  }

  protected XPathExpr xpath(String expression) throws XPathExpressionException {
    return XPathFactory.newXPath(expression);
  }
}
