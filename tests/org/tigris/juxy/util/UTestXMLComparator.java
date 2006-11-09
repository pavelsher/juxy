package org.tigris.juxy.util;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;

/**
 * $Id: UTestXMLComparator.java,v 1.5 2006-11-09 17:28:07 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestXMLComparator extends TestCase {
  public void testAssertPassed() throws SAXException {
    XMLComparator.assertEquals("<root/>", DOMUtil.parse("<root/>"));
    XMLComparator.assertEquals("<root></root>", DOMUtil.parse("<root/>"));
    XMLComparator.assertEquals("<root><child/>  </root>", DOMUtil.parse("<root><child/></root>"));
    XMLComparator.assertEquals("<root>some text</root>", DOMUtil.parse("<root>some text</root>"));
    XMLComparator.assertEquals("<root xmlns='urn:some:ns'/>", DOMUtil.parse("<root xmlns='urn:some:ns'/>"));
    XMLComparator.assertEquals("<root><!-- comment --></root>", DOMUtil.parse("<root><!-- comment --></root>"));
    XMLComparator.assertEquals("<root><?pi a='b'?></root>", DOMUtil.parse("<root><?pi a='b'?></root>"));
    XMLComparator.assertEquals("<root>some text<elem/> text continued</root>", DOMUtil.parse("<root>some text<elem/> text continued</root>"));
    XMLComparator.assertEquals("<root attr1='val1' attr2='val2'/>", DOMUtil.parse("<root attr2=\"val2\" attr1=\"val1\"></root>"));
    XMLComparator.assertEquals("<root xmlns:ns1='urn:some:ns' ns1:attr1='val1' attr2='val2'/>", DOMUtil.parse("<root xmlns:ns1='urn:some:ns' attr2=\"val2\" ns1:attr1=\"val1\"></root>"));
    XMLComparator.assertEquals("<root><![CDATA[text]]></root>", DOMUtil.parse("<root><![CDATA[text]]></root>"));
  }

  public void testAssertPassed_Doctype() throws SAXException {
    XMLComparator.assertEquals("" +
        "<!DOCTYPE sect [" +
        "   <!ELEMENT sect EMPTY>" +
        "]>" +
        "<sect/>",
        DOMUtil.parse("" +
            "<!DOCTYPE sect [" +
            "   <!ELEMENT sect EMPTY>" +
            "]>" +
            "<sect/>"));

    String system = new File("tests/xml/sect.dtd").toURI().toString();

    XMLComparator.assertEquals("" +
        "<!DOCTYPE sect PUBLIC 'urn1' '" + system + "'>" +
        "<sect/>",
        DOMUtil.parse("" +
            "<!DOCTYPE sect PUBLIC 'urn1' '" + system + "'>" +
            "<sect/>"));
    XMLComparator.assertEquals("" +
        "<!DOCTYPE sect SYSTEM '" + system + "'>" +
        "<sect/>",
        DOMUtil.parse("" +
            "<!DOCTYPE sect SYSTEM '" + system + "'>" +
            "<sect/>"));
    XMLComparator.assertEquals("" +
        "<!DOCTYPE sect [" +
        "<!ENTITY ent1 'enttext1'>" +
        "]>" +
        "<sect>&ent1;</sect>",
        DOMUtil.parse("" +
            "<!DOCTYPE sect [" +
            "<!ENTITY ent1 'enttext1'>" +
            "]>" +
            "<sect>&ent1;</sect>"));
  }

  public void testAssertionFailed_ElementsAndAttributes() throws SAXException {
    try {
      XMLComparator.assertEquals("<root/>", DOMUtil.parse("<root attr='value'/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root attr='value'/>", DOMUtil.parse("<root attr='value' attr2='val2'/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root attr1='value1'/>", DOMUtil.parse("<root attr2='value2'/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root attr='value1'/>", DOMUtil.parse("<root attr='value with \" char'/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root><child/></root>", DOMUtil.parse("<root><child2/></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root><child><subchild/></child></root>", DOMUtil.parse("<root><child/></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root><child1/><child2/></root>", DOMUtil.parse("<root><child1/><child3/></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }
  }

  public void testNamespacesAssertion() throws SAXException {
    try {
      XMLComparator.assertEquals("<root/>", DOMUtil.parse("<root xmlns='urn:some:ns'/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    XMLComparator.assertEquals("<root/>", DOMUtil.parse("<root xmlns=''/>"));

    try {
      XMLComparator.assertEquals("<root xmlns:ns1='urn:some:ns' ns1:attr1='val1' attr2='val2'/>", DOMUtil.parse("<root xmlns:ns1='urn:some:ns2' attr2=\"val2\" ns1:attr1=\"val1\"></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }
  }

  public void testAssertionFailed_Doctype() throws SAXException {
    try {
      XMLComparator.assertEquals("" +
          "<!DOCTYPE sect [" +
          "   <!ELEMENT sect EMPTY>" +
          "]>" +
          "<sect/>",
          DOMUtil.parse("" +
              "<!DOCTYPE sect [" +
              "   <!ELEMENT sect EMPTY>" +
              "   <!ENTITY ent \"enttext\">" +
              "]>" +
              "<sect/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    String system = new File("tests/xml/sect.dtd").toURI().toString();

    try {
      XMLComparator.assertEquals("" +
          "<!DOCTYPE sect PUBLIC 'urn1' '" + system + "'>" +
          "<sect/>",
          DOMUtil.parse("" +
              "<!DOCTYPE sect PUBLIC 'urn2' '" + system + "'>" +
              "<sect/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("" +
          "<!DOCTYPE sect SYSTEM '" + system + "'>" +
          "<sect/>",
          DOMUtil.parse("" +
              "<!DOCTYPE sect PUBLIC 'urn2' '" + system + "'>" +
              "<sect/>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("" +
          "<!DOCTYPE sect [" +
          "<!ENTITY ent1 'enttext1'>" +
          "<!ENTITY ent2 'enttext2'>" +
          "]>" +
          "<sect>&ent1;</sect>",
          DOMUtil.parse("" +
              "<!DOCTYPE sect [" +
              "<!ENTITY ent1 'enttext1'>" +
              "<!ENTITY ent2 'enttext2'>" +
              "]>" +
              "<sect>&ent2;</sect>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }
  }

  public void testAssertFailed_Other() throws SAXException {
    try {
      XMLComparator.assertEquals("<root/>", DOMUtil.parse("<root>text with &lt; char</root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }


    try {
      XMLComparator.assertEquals("<root><!-- comment --></root>", DOMUtil.parse("<root><!-- different comment --></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root><!-- comment --></root>", DOMUtil.parse("<root><child/></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root><?pi a='b'?></root>", DOMUtil.parse("<root><?pi a='c'?></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }

    try {
      XMLComparator.assertEquals("<root><![CDATA[text]]></root>", DOMUtil.parse("<root><![CDATA[text2]]></root>"));
      fail("An exception expected");
    } catch (DocumentsAssertionError e) {
      System.out.println(e.getMessage());
    }
  }

  public void testPassed_DocumentPartsCompared() throws SAXException {
    Document actual = DOMUtil.parse("<parent><child1/><child2/></parent>");
    XMLComparator.assertEquals("<child1/>", actual.getDocumentElement().getFirstChild());
    XMLComparator.assertEquals("<child2/>", actual.getDocumentElement().getLastChild());
  }
}
