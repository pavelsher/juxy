package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.Tracer;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.util.XSLTEngineSupport;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.*;

/**
 * @author Pavel Sher
 */
public class TracingFilter extends XMLFilterImpl {
  private final static Log logger = LogFactory.getLog(TracingFilter.class);
  private Locator locator;
  private List saxEvents = new ArrayList(20);
  private Map namespaces = new HashMap();
  private boolean withinTemplateElement = false;
  private static int MAX_TEXT_LEN = 51;
  private XSLTEngineSupport engineSupport;

  public TracingFilter(final XSLTEngineSupport engineSupport) {
    this.engineSupport = engineSupport;
  }

  public void startDocument() throws SAXException {
    logger.debug("Start augmenting stylesheet with tracing code: " + locator.getSystemId() + " ...");
    super.startDocument();
  }

  public void endDocument() throws SAXException {
    logger.debug("End augmenting stylesheet with tracing code");
    super.endDocument();
  }

  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
    super.setDocumentLocator(locator);
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    try {
      if (!isTemplateElement(uri, localName) && !withinTemplateElement) {
        super.startElement(uri, localName, qName, atts);
        return;
      }

      withinTemplateElement = true;
      appendSAXEvent(new StartElementEvent(uri, localName, qName, atts));
    } finally {
      namespaces.clear();
    }
  }

  private void startElement0(String uri, String localName, String qName, Attributes atts) throws SAXException {
    super.startElement(uri, localName, qName, atts);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (!withinTemplateElement) {
      super.endElement(uri, localName, qName);
      return;
    }

    appendSAXEvent(new EndElementEvent(uri, localName, qName));

    if (isTemplateElement(uri, localName)) {
      withinTemplateElement = false;
      flushEvents();
    }
  }

  private void endElement0(String uri, String localName, String qName) throws SAXException {
    super.endElement(uri, localName, qName);
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    namespaces.put(prefix, uri);
    super.startPrefixMapping(prefix, uri);
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    namespaces.remove(prefix);
    super.endPrefixMapping(prefix);
  }

  public void startPrefixMapping0(String prefix, String uri) throws SAXException {
    super.startPrefixMapping(prefix, uri);
  }

  public void endPrefixMapping0(String prefix) throws SAXException {
    super.endPrefixMapping(prefix);
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    appendSAXEvent(new CharactersEvent(ch, start, length));
  }

  private void characters0(char ch[], int start, int length) throws SAXException {
    super.characters(ch, start, length);
  }

  public void processingInstruction(String target, String data) throws SAXException {
    appendSAXEvent(new PiEvent(target, data));
  }

  public void processingInstruction0(String target, String data) throws SAXException {
    super.processingInstruction(target, data);
  }

  private void flushEvents() throws SAXException {
    if (saxEvents.size() == 0) return;
    LinkedList allEvents = new LinkedList();
    List postponedEvents = new ArrayList(20);
    int i = 0;
    int level = 0;
    boolean inXslText = false;
    while (i < saxEvents.size()) {
      SAXEvent event = (SAXEvent) saxEvents.get(i);
      if (event instanceof StartElementEvent) {
        level++;
        StartElementEvent startEvent = (StartElementEvent) event;

        inXslText = isXslTextElement(startEvent.uri, startEvent.localName);

        if (isTemplateElement(startEvent.uri, startEvent.localName)) {
          allEvents.add(startEvent);
          postponedEvents.addAll(generateTracingEvents(tagName(startEvent), startEvent, level));
          i++;
        } else {
          if (isParamElement(startEvent.uri, startEvent.localName)) {
            allEvents.add(startEvent);
            i++;
            continue;
          } else {
            allEvents.addAll(postponedEvents);
            postponedEvents.clear();
          }

          if (isAugmented(startEvent.uri, startEvent.localName) && !isAugmentedAfterStart(startEvent.uri, startEvent.localName))
            allEvents.addAll(generateTracingEvents(tagName(startEvent), startEvent, level));

          allEvents.add(startEvent);

          if (isAugmented(startEvent.uri, startEvent.localName) && isAugmentedAfterStart(startEvent.uri, startEvent.localName))
            allEvents.addAll(generateTracingEvents(tagName(startEvent), startEvent, level));

          i++;
        }
      }

      if (event instanceof EndElementEvent) {
        EndElementEvent endEvent = (EndElementEvent) event;
        if (isXslTextElement(endEvent.uri, endEvent.localName)) {
          inXslText = false;
          allEvents.add(event);
        }

        if (!isParamElement(endEvent.uri, endEvent.localName) && !inXslText) {
          allEvents.addAll(postponedEvents);
          postponedEvents.clear();
        }

        if (!isXslTextElement(endEvent.uri, endEvent.localName))
          allEvents.add(event);

        level--;
        i++;
      }

      if (event instanceof CharactersEvent) {
        List generatedEvents = generateCharactersTracingEvents(i, level);
        if (generatedEvents.size() > 0) {
          if (inXslText) {
            postponedEvents.addAll(generatedEvents);
          } else {
            allEvents.addAll(postponedEvents);
            postponedEvents.clear();
            allEvents.addAll(generatedEvents);
          }
        }

        allEvents.add(event);
        i++;
        for (; i < saxEvents.size(); i++) {
          SAXEvent ev = (SAXEvent) saxEvents.get(i);
          if (!(ev instanceof CharactersEvent))
            break;

          allEvents.add(ev);
        }
      }

      if (event instanceof PiEvent) {
        postponedEvents.addAll(generateTracingEvents((PiEvent) event, level));
        allEvents.add(event);

        i++;
      }
    }

    Iterator it = allEvents.iterator();
    while (it.hasNext()) {
      SAXEvent event = (SAXEvent) it.next();
      event.generate();
    }

    saxEvents.clear();
  }

  private List generateCharactersTracingEvents(int startIdx, int level) {
    List result = new ArrayList(5);

    StringBuffer text = new StringBuffer(30);
    int line = -1;
    for (int i = startIdx; i < saxEvents.size(); i++) {
      SAXEvent e = (SAXEvent) saxEvents.get(i);
      if (!(e instanceof CharactersEvent))
        break;

      CharactersEvent ce = (CharactersEvent) e;
      if (line == -1)
        line = ce.line;

      String et = ce.getString();
      text.append(StringUtil.collapseSpaces(et, StringUtil.SPACE_AND_CARRIAGE_CHARS));

      if (i + 1 >= saxEvents.size() || !(saxEvents.get(i + 1) instanceof CharactersEvent)) {
        if (text.length() >= MAX_TEXT_LEN) {
          text.delete(MAX_TEXT_LEN - 1, text.length());
          text.append(" ...");
        }

        String trimmedText = text.toString().trim();
        if (trimmedText.length() > 0)
          result.addAll(generateTracingEvents(trimmedText, line, ce.systemId, level));
      }
    }

    return result;
  }

  private List generateTracingEvents(PiEvent event, int level) {
    return generateTracingEvents("<?" + event.target + (event.data.length() > 0 ? " " + event.data : "") + "?>", event, level);
  }

  private List generateTracingEvents(String tracingText, SAXEvent event, int level) {
    return generateTracingEvents(tracingText, event.line, event.systemId, level);
  }

  private List generateTracingEvents(String tracingText, int line, String systemId, int level) {
    List events = new ArrayList(6);
    AttributesImpl a = new AttributesImpl();
    a.addAttribute("", "select", "select", "CDATA",
        JuxyParams.TRACER_EXTENSION_NAME + ":trace(" +
            line + ", " +
            level + ", '" +
            escapeSingleQuot(systemId) + "', '" +
            escapeSingleQuot(tracingText) + "')");

    events.add(new StartPrefixMappingEvent(JuxyParams.TRACER_EXTENSION_NAME, engineSupport.getJavaExtensionNamespace(Tracer.class)));
    events.add(new StartElementEvent(XSLTKeys.XSLT_NS, "value-of", "xsl:value-of", a));
    events.add(new EndElementEvent(XSLTKeys.XSLT_NS, "value-of", "xsl:value-of"));
    events.add(new EndPrefixMappingEvent(JuxyParams.TRACER_EXTENSION_NAME));
    return events;
  }

  private String tagName(StartElementEvent startEvent) {
    StringBuffer msg = new StringBuffer(10);
    msg.append("<").append(startEvent.qName);
    Iterator it = startEvent.elemNamespaces.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry e = (Map.Entry) it.next();
      msg.append(" xmlns:").append(e.getKey()).append("=\"").append(escapeSingleQuot((String) e.getValue())).append("\"");
    }

    for (int i = 0; i < startEvent.atts.getLength(); i++) {
      msg.append(" ").append(startEvent.atts.getQName(i));
      msg.append("=\"").append(startEvent.atts.getValue(i)).append("\"");
    }
    msg.append(">");
    return msg.toString();
  }

  private String escapeSingleQuot(String str) {
    return StringUtil.replaceCharByEntityRef(str, '\'');
  }

  private void appendSAXEvent(SAXEvent event) {
    saxEvents.add(event);
  }

  private boolean isTemplateElement(String uri, String localName) {
    return XSLTKeys.XSLT_NS.equals(uri) && localName.equals("template");
  }

  private boolean isXslTextElement(String uri, String localName) {
    return XSLTKeys.XSLT_NS.equals(uri) && localName.equals("text");
  }

  private boolean isParamElement(String uri, String localName) {
    return XSLTKeys.XSLT_NS.equals(uri) && localName.equals("param");
  }

  private boolean isAugmented(String uri, String localName) {
    return !(XSLTKeys.XSLT_NS.equals(uri) && NOT_AUGMENTED_STATEMENTS.contains(localName));
  }

  private boolean isAugmentedAfterStart(String uri, String localName) {
    return XSLTKeys.XSLT_NS.equals(uri) && AUGMENTED_AFTER_START.contains(localName);
  }

  private static final Set AUGMENTED_AFTER_START = new HashSet();

  static {
    AUGMENTED_AFTER_START.add("for-each");
    AUGMENTED_AFTER_START.add("for-each-group");
    AUGMENTED_AFTER_START.add("otherwise");
    AUGMENTED_AFTER_START.add("template");
    AUGMENTED_AFTER_START.add("when");
    AUGMENTED_AFTER_START.add("matching-substring");
    AUGMENTED_AFTER_START.add("non-matching-substring");
    AUGMENTED_AFTER_START.add("fallback");
    AUGMENTED_AFTER_START.add("attribute");
    AUGMENTED_AFTER_START.add("namespace");
  }

  private static final Set NOT_AUGMENTED_STATEMENTS = new HashSet();

  static {
    NOT_AUGMENTED_STATEMENTS.add("with-param");
    NOT_AUGMENTED_STATEMENTS.add("param");
    NOT_AUGMENTED_STATEMENTS.add("sort");
  }

  abstract class SAXEvent {
    public int line;
    public int column;
    public String systemId;

    public SAXEvent() {
      this.line = locator.getLineNumber();
      this.column = locator.getColumnNumber();
      this.systemId = locator.getSystemId();
    }

    public abstract void generate() throws SAXException;
  }

  abstract class ElementEvent extends SAXEvent {
    public String uri;
    public String localName;
    public String qName;

    public ElementEvent(String uri, String localName, String qName) {
      this.uri = uri;
      this.localName = localName;
      this.qName = qName;
    }
  }

  class StartElementEvent extends ElementEvent {
    public Attributes atts;
    public Map elemNamespaces = new HashMap();

    public StartElementEvent(String uri, String localName, String qName, Attributes atts) {
      super(uri, localName, qName);
      this.atts = new AttributesImpl(atts);
      this.elemNamespaces.putAll(namespaces);
    }

    public void generate() throws SAXException {
      startElement0(uri, localName, qName, atts);
    }
  }

  class EndElementEvent extends ElementEvent {
    public EndElementEvent(String uri, String localName, String qName) {
      super(uri, localName, qName);
    }

    public void generate() throws SAXException {
      endElement0(uri, localName, qName);
    }
  }

  class StartPrefixMappingEvent extends SAXEvent {
    public String prefix;
    public String uri;

    public StartPrefixMappingEvent(String prefix, String uri) {
      this.prefix = prefix;
      this.uri = uri;
    }

    public void generate() throws SAXException {
      startPrefixMapping0(prefix, uri);
    }
  }

  class EndPrefixMappingEvent extends SAXEvent {
    public String prefix;

    public EndPrefixMappingEvent(String prefix) {
      this.prefix = prefix;
    }

    public void generate() throws SAXException {
      endPrefixMapping0(prefix);
    }
  }

  class CharactersEvent extends SAXEvent {
    public char[] chars;
    public int start;
    public int length;

    public CharactersEvent(char ch[], int start, int length) {
      this.chars = new char[length];
      System.arraycopy(ch, start, chars, 0, length);
      this.start = 0;
      this.length = length;
    }

    public String getString() {
      StringBuffer buf = new StringBuffer(20);
      for (int i = start; i < start + length; i++) {
        buf.append(chars[i]);
      }

      return buf.toString();
    }

    public void generate() throws SAXException {
      characters0(chars, start, length);
    }
  }

  class PiEvent extends SAXEvent {
    public String target;
    public String data;

    public PiEvent(String target, String data) {
      this.target = target;
      this.data = data;
    }

    public void generate() throws SAXException {
      processingInstruction0(target, data);
    }
  }
}
