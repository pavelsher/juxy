package org.tigris.juxy.validator;

import junit.framework.TestCase;
import org.tigris.juxy.util.DOMUtil;

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
    } catch (ValidationFailedException e) {
      e.printStackTrace();
    }
  }

  public void testLoadSchemaFromResources() throws Exception {
    Validator validator =
        ValidatorFactory.createXMLSchemaValidator("/xml/validator/schema1.xml");
    validator.validate(DOMUtil.parse("<name>some text</name>"));
  }

  public void testBadSchema() throws Exception {
    try {
      ValidatorFactory.createXMLSchemaValidator("tests/xml/validator/badSchema.xml");
      fail("An exception expected");
    } catch (Throwable t) {
      t.printStackTrace();
    }

    try {
      ValidatorFactory.createXMLSchemaValidator("tests/xml/validator/nonExistentSchema.xml");
      fail("An exception expected");
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}
