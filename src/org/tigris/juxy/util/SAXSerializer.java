package org.tigris.juxy.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Serializes incoming SAX events into the specified output stream.
 * <p/>
 * Serializer does not support internal DTD subset, doctypes and notations.
 *
 * @author Pavel Sher
 */
public class SAXSerializer extends XMLFilterImpl {
  private PrintWriter writer;
  private Map namespaces = new HashMap();
  private Stack elemContents = new Stack();
  private Stack entities = new Stack();
  private boolean withinCDATA = false;
  private List currentNamespaces = new ArrayList();

  public void setOutputStream(OutputStream os) {
    writer = new PrintWriter(os);
  }

  public void startDocument() throws SAXException {
    super.startDocument();
  }

  public void endDocument() throws SAXException {
    super.endDocument();
    getWriter().flush();
    getWriter().close();
  }

  private PrintWriter getWriter() {
    if (writer == null)
      throw new IllegalArgumentException("OutputStream is not set");
    return writer;
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    super.startPrefixMapping(prefix, uri);
    namespaces.put(uri, prefix);
    currentNamespaces.add(uri);
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    super.endPrefixMapping(prefix);
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    super.startElement(uri, localName, qName, atts);
    if (!withinEntity()) {
      completeStartingTagAndUpdateCounter();
      String name = getElementName(localName, qName);

      getWriter().print("<" + name);
      printXMLNSAttributes();

      if (atts.getLength() > 0)
        getWriter().print(" ");
      for (int i = 0; i < atts.getLength(); i++) {
        String aname = atts.getQName(i);
        String avalue = encode(atts.getValue(i), true);
        getWriter().print(aname + "=\"" + avalue + "\"");
        if (i + 1 < atts.getLength())
          getWriter().print(" ");
      }
      elemContents.push(new Integer(0));
      getWriter().flush();
    }
  }

  private void printXMLNSAttributes() {
    Iterator nsIt = currentNamespaces.iterator();
    while (nsIt.hasNext()) {
      String uri = (String) nsIt.next();
      String prefix = (String) namespaces.get(uri);

      String result = " xmlns";
      if (prefix != null && prefix.length() > 0)
        result += ":" + prefix;
      result += "=\"" + uri + "\"";
      getWriter().write(result);
      nsIt.remove();
    }
  }

  private String encode(String str, boolean encodeQuote) {
    String result = StringUtil.escapeXMLText(str);
    if (encodeQuote)
      result = StringUtil.escapeQuoteCharacter(result);
    return result;
  }

  private String getElementName(String localName, String qName) {
    return qName.length() > 0 ? qName : localName;
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    super.endElement(uri, localName, qName);
    if (!withinEntity()) {
      Integer counter = (Integer) elemContents.pop();
      if (counter.intValue() == 0)
        getWriter().print("/>");
      else
        getWriter().print("</" + getElementName(localName, qName) + ">");
      getWriter().flush();
    }
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    super.characters(ch, start, length);
    if (!withinEntity()) {
      completeStartingTagAndUpdateCounter();
      printCharacters(ch, start, length, !withinCDATA);
      getWriter().flush();
    }
  }

  private void completeStartingTagAndUpdateCounter() {
    if (elemContents.size() > 0) {
      Integer counter = (Integer) elemContents.pop();
      if (counter.intValue() == 0)
        getWriter().print(">");
      elemContents.push(new Integer(counter.intValue() + 1));
    }
  }

  private void printCharacters(char[] ch, int start, int length, boolean withEncode) {
    if (withEncode)
      getWriter().print(encodeCharacters(ch, start, length));
    else
      for (int i = 0; i < length; i++)
        getWriter().print(ch[start + i]);
  }

  private String encodeCharacters(char[] ch, int start, int length) {
    StringBuffer result = new StringBuffer(length);
    for (int i = 0; i < length; i++) {
      int pos = start + i;
      if (ch[pos] == '<')
        result.append("&lt;");
      else if (ch[pos] == '&')
        result.append("&amp;");
      else result.append(ch[pos]);
    }

    return result.toString();
  }

  public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    super.ignorableWhitespace(ch, start, length);
    if (!withinEntity()) {
      completeStartingTagAndUpdateCounter();
      printCharacters(ch, start, length, false);
      getWriter().flush();
    }
  }

  public void processingInstruction(String target, String data) throws SAXException {
    super.processingInstruction(target, data);
    if (!withinEntity()) {
      completeStartingTagAndUpdateCounter();
      getWriter().print("<?" + target + " " + encode(data, false) + "?>");
      getWriter().flush();
    }
  }

/*
    public void startDTD(String name, String publicId, String systemId) throws SAXException
    {
        if (!withinEntity()) {
            getWriter().print("<!DOCTYPE " + name + " ");
            if (publicId != null)
                getWriter().print("PUBLIC \"" + publicId + "\" \"" + systemId + "\"");
            else if (systemId != null)
                getWriter().print("SYSTEM \"" + systemId + "\"");
            getWriter().flush();
        }
    }
*/

/*
    public void endDTD() throws SAXException
    {
        if (!withinEntity()) {
            getWriter().print(">");
            getWriter().flush();
        }
    }
*/

/*
    public void startEntity(String name) throws SAXException
    {
        // skip system identifiers
        if (!name.startsWith("[")) {
            if (!withinEntity()) {
                completeStartingTagAndUpdateCounter();
                getWriter().print("&" + name);
                getWriter().flush();
            }

            entities.push(name);
        }
    }
*/

/*
    public void endEntity(String name) throws SAXException
    {
        if (!name.startsWith("[")) {
            entities.pop();
            if (!withinEntity()) {
                getWriter().print(";");
                getWriter().flush();
            }
        }
    }
*/

/*
    public void startCDATA() throws SAXException
    {
        completeStartingTagAndUpdateCounter();
        withinCDATA = true;
        getWriter().print("<![CDATA[");
        getWriter().flush();
    }

    public void endCDATA() throws SAXException
    {
        getWriter().print("]]>");
        getWriter().flush();
        withinCDATA = false;
    }
*/

/*
    public void comment(char ch[], int start, int length) throws SAXException
    {
        completeStartingTagAndUpdateCounter();
        getWriter().print("<!--");
        printCharacters(ch, start, length, false);
        getWriter().print("-->");
        getWriter().flush();
    }
*/

  private boolean withinEntity() {
    return !entities.isEmpty();
  }
}
