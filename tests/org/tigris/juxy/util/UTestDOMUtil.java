package org.tigris.juxy.util;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * $Id: UTestDOMUtil.java,v 1.2 2006-10-31 11:01:24 pavelsher Exp $
 * <p/>
 * Created: 13.04.2005
 *
 * @author Pavel Sher
 */
public class UTestDOMUtil extends TestCase {
  public void testGetInnerTextSimpleDOM() throws SAXException {
    Document doc = DOMUtil.parse("<root>a text</root>");
    assertEquals("a text", DOMUtil.innerText(doc));
  }

  public void testGetInnerTextComplexDOM() throws SAXException {
    Document doc = DOMUtil.parse("" +
        "<root>a complex\n" +
        "<em>text</em> with a HTML like list:" +
        "<ol>" +
        "   <li>item1</li>" +
        "   <li>item2</li>" +
        "</ol>" +
        "</root>");
    assertEquals("a complex text with a HTML like list: item1 item2", StringUtil.normalizeAll(DOMUtil.innerText(doc)));
  }
}
