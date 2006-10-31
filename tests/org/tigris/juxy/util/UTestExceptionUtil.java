package org.tigris.juxy.util;

import junit.framework.TestCase;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 */
public class UTestExceptionUtil extends TestCase {
  public void testWithoutLocator() {
    TransformerException ex = new TransformerException("an exception");
    assertEquals("an exception", ExceptionUtil.exceptionToString(ex, true));
  }

  public void testWithLocator_ShowSystemId() {
    TransformerException ex = new TransformerException("an exception", new LocatorImpl("file.xsl", 1, 2));
    assertEquals("line#: 1, col#: 2; an exception", ExceptionUtil.exceptionToString(ex, false));
    assertEquals("file.xsl, line#: 1, col#: 2; an exception", ExceptionUtil.exceptionToString(ex, true));
  }

  public void testWithLocator_LineOrColumnOmitted() {
    TransformerException ex = new TransformerException("an exception", new LocatorImpl("file.xsl", -1, 1));
    assertEquals("file.xsl, col#: 1; an exception", ExceptionUtil.exceptionToString(ex, true));

    ex = new TransformerException("an exception", new LocatorImpl("file.xsl", 1, -1));
    assertEquals("file.xsl, line#: 1; an exception", ExceptionUtil.exceptionToString(ex, true));

    ex = new TransformerException("an exception", new LocatorImpl("file.xsl", -1, -1));
    assertEquals("file.xsl; an exception", ExceptionUtil.exceptionToString(ex, true));
  }

  public void testSAXParseException_NullLocator() {
    SAXParseException ex = new SAXParseException("an exception", null);
    assertEquals("an exception", ExceptionUtil.exceptionToString(ex, true));
  }

  public void testSAXParseException_SystemId() {
    SAXParseException ex = new SAXParseException("an exception", new LocatorImpl("file.xsl", 1, 2));
    assertEquals("file.xsl, line#: 1, col#: 2; an exception", ExceptionUtil.exceptionToString(ex, true));
    assertEquals("line#: 1, col#: 2; an exception", ExceptionUtil.exceptionToString(ex, false));

    ex = new SAXParseException("an exception", new LocatorImpl(null, 1, 2));
    assertEquals("line#: 1, col#: 2; an exception", ExceptionUtil.exceptionToString(ex, true));
  }

  private class LocatorImpl implements SourceLocator, Locator {
    public String systemId;
    public int line;
    public int column;

    public LocatorImpl(String systemId, int line, int column) {
      this.systemId = systemId;
      this.line = line;
      this.column = column;
    }

    public String getPublicId() {
      return null;
    }

    public String getSystemId() {
      return systemId;
    }

    public int getLineNumber() {
      return line;
    }

    public int getColumnNumber() {
      return column;
    }
  }
}
