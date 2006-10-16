package org.tigris.juxy.validator;

import junit.framework.TestCase;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathAssert;
import org.w3c.dom.Node;

/**
 * User: pavel
 * Date: 10.10.2006
 */
public class UTestValidator extends TestCase {
  public void testSchemaValidation() throws Exception {
    Validator validator =
        ValidatorFactory.createXMLSchemaValidator("tests/xml/validator/schema1.xml");
    validator.validate(DOMUtil.parse("<name>some text</name>"));

    try {
      validator.validate(DOMUtil.parse("<name1>some text</name1>"));
      fail("An exception expected");
    } catch (ValidationFailedException e) {}
  }

  public void testBadSchema() throws Exception {
    try {
      ValidatorFactory.createXMLSchemaValidator("tests/xml/validator/badSchema.xml");
      fail("An exception expected");
    } catch (Throwable t) {}

    try {
      ValidatorFactory.createXMLSchemaValidator("tests/xml/validator/nonExistentSchema.xml");
      fail("An exception expected");
    } catch (Throwable t) {}
  }

  public void testXPathValidation() throws Exception {
    Validator validator =
        ValidatorFactory.createXPathValidator(new XPathAssert[] {
            new XPathAssert("count(//li)", 2),
            new XPathAssert("/ul/li[@param]/@param", "value")
        });
    validator.validate(DOMUtil.parse("<ul><li param='value'/><li/></ul>"));

    try {
      validator.validate(DOMUtil.parse("<ul><li/><li/><li/></ul>"));
      fail("An exception expected");
    } catch (ValidationFailedException e) {}

    Node node = DOMUtil.parse("<value>5.002</value>");
    new XPathAssert("/value/text()", 5.0, 0.005).eval(node);
    new XPathAssert("/value/text()", 5.1, 0.1).eval(node);
    new XPathAssert("/value/text()", 4.91, 0.1).eval(node);
    try {
      new XPathAssert("/value/text()", 4.9, 0.1).eval(node);
      fail("An exception expected");
    } catch (AssertionError error) {
      System.err.println(error);
    }
  }
}
