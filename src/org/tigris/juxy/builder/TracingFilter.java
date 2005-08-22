package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.Tracer;
import org.tigris.juxy.util.StringUtil;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.*;

/**
 * $Id: TracingFilter.java,v 1.3 2005-08-22 07:46:15 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class TracingFilter extends XMLFilterImpl {
    private final static Log logger = LogFactory.getLog(TracingFilter.class);
    private Locator locator;
    private int level = 0;
    private boolean withinTemplate;

    private List activeQueue = new ArrayList(3);
    private List templateEvents = new ArrayList(3);
    private List rawEvents = new ArrayList(3);

    private static int MAX_TEXT_LEN = 51;
    private boolean withinXslText = false;

    public void startDocument() throws SAXException {
        logger.info("Start augmenting stylesheet with tracing code: " + locator.getSystemId() + " ...");
        super.startDocument();
        super.startPrefixMapping(JuxyParams.PREFIX, JuxyParams.NS);
        super.startPrefixMapping(JuxyParams.TRACE_PARAM, "java:" + Tracer.class.getName());
    }

    public void endDocument() throws SAXException {
        logger.info("End augmenting stylesheet with tracing code");
        super.endDocument();
    }

    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }

    private void superStartElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);
    }

    private void superEndElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    private void superCharacters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    private void superProcessingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
    }

    private void pushStartElement(List queue, String uri, String localName, String qName, Attributes atts) {
        queue.add(new StartElementEvent(uri, localName, qName, atts));
    }

    private void pushEndElement(List queue, String uri, String localName, String qName) {
        queue.add(new EndElementEvent(uri, localName, qName));
    }

    private void pushCharacters(List queue, char[] ch, int start, int length) {
        queue.add(new CharactersEvent(ch, start, length));
    }

    private void pushProcessingInstruction(List queue, String target, String data) {
        queue.add(new PiEvent(target, data));
    }

    private void pushEvents(List queue, List events) {
        queue.addAll(events);
    }

    private void flushActiveQueue() throws SAXException {
        flushQueue(activeQueue);
    }

    private void flushTemplateEvents() throws SAXException {
        flushQueue(templateEvents);
    }

    private void flushQueue(List queue) throws SAXException {
        for (int i=0; i<queue.size(); i++) {
            Event e = (Event) queue.get(i);
            e.generate();
        }
        queue.clear();
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (isStylesheetElement(uri, localName)) {
            level = 0;
            String excludedPrefixes = atts.getValue("exclude-result-prefixes");
            if (excludedPrefixes == null) excludedPrefixes = "";
            excludedPrefixes += " " + JuxyParams.PREFIX + " " + JuxyParams.TRACE_PARAM;
            pushStartElement(activeQueue, uri, localName, qName, new AttributesImpl(atts));
        } else {
            level++;
            if (isTemplateElement(uri, localName)) {
                withinTemplate = true;
                pushEvents(templateEvents, generateTracing(tagToString(qName, atts)));
                pushStartElement(activeQueue, uri, localName, qName, atts);
            } else {
                if (!isParamElement(localName))
                    flushTemplateEvents();

                String tag = tagToString(qName, atts);
                if (isAugmented(localName) && !isAugmentedAfterStart(localName))
                    pushEvents(activeQueue, generateTracing(tag));

                pushStartElement(activeQueue, uri, localName, qName, atts);

                if (isAugmented(localName) && isAugmentedAfterStart(localName))
                    pushEvents(activeQueue, generateTracing(tag));
            }
        }

        generateAndFlushRawEvents();
        flushActiveQueue();

        if (isXslTextElement(uri, localName))
            withinXslText = true;
    }

    private boolean isXslTextElement(String uri, String localName) {
        return XSLTKeys.XSLT_NS.equals(uri) && localName.equals("text");
    }

    private boolean isParamElement(String localName) {
        return localName.equals("param");
    }

    private boolean isStylesheetElement(String uri, String localName) {
        return XSLTKeys.XSLT_NS.equals(uri) && localName.equals("stylesheet");
    }

    private boolean isTemplateElement(String uri, String localName) {
        return XSLTKeys.XSLT_NS.equals(uri) && localName.equals("template");
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (isTemplateElement(uri, localName))
                flushTemplateEvents();

            if (isXslTextElement(uri, localName)) {
                withinXslText = false;
                List result = generateTracingForRawEvents();
                flushRawEvents();
                super.endElement(uri, localName, qName);
                flushQueue(result);
                return;
            }

            generateAndFlushRawEvents();

            super.endElement(uri, localName, qName);
        } finally {
            if (isTemplateElement(uri, localName))
                withinTemplate = false;
            level--;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        pushCharacters(rawEvents, ch, start, length);
    }

    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        pushProcessingInstruction(rawEvents, target, data);
    }

    private void flushRawEvents() throws SAXException {
        flushQueue(rawEvents);
    }

    private void generateAndFlushRawEvents() throws SAXException {
        if (isAugmentationAllowed()) {
            flushQueue(generateTracingForRawEvents());
            flushQueue(rawEvents);
        }
    }

    private List generateTracingForRawEvents() {
        List result = new ArrayList(5);

        if (isAugmentationAllowed()) {
            int lineNum = -1;
            int level = -1;
            StringBuffer text = new StringBuffer(30);
            for (int i=0; i<rawEvents.size(); i++) {
                Event e = (Event) rawEvents.get(i);
                if (e instanceof PiEvent) {
                    PiEvent pe = (PiEvent) e;
                    result.addAll(generateTracing("<?" + pe.target + " " + pe.data + "?>", pe.getStartLineNum(), pe.getLevel()));
                } else {
                    CharactersEvent ce = (CharactersEvent) e;
                    if (i == 0) {
                        lineNum = ce.getStartLineNum();
                        level = ce.getLevel();
                    }
                    String et = ce.toString();
                    text.append(StringUtil.collapseSpaces(et, StringUtil.SPACE_AND_CARRIAGE_CHARS));

                    if (i+1 >= rawEvents.size() || !(rawEvents.get(i+1) instanceof CharactersEvent)) {
                        if (text.length() >= MAX_TEXT_LEN) {
                            text.delete(MAX_TEXT_LEN - 1, text.length());
                            text.append(" ...");
                        }

                        String trimmedText = text.toString().trim();
                        if (trimmedText.length() > 0)
                            result.addAll(generateTracing(trimmedText, lineNum, level));
                    }
                }
            }
        }

        return result;
    }

    private List generateTracing(String text) {
        return generateTracing(text, locator.getLineNumber(), level);
    }

    private List generateTracing(String text, int lineNum, int level) {
        List events = new ArrayList(2);
        AttributesImpl a = new AttributesImpl();
        a.addAttribute("", "select", "select", "CDATA",
                "tracer:trace($" + JuxyParams.PREFIX + ":" + JuxyParams.TRACE_PARAM + ", " +
                            lineNum + ", " +
                            level + ", '" +
                            escapeSingleQuot(locator.getSystemId()) + "', '" +
                            escapeSingleQuot(text) + "')");

        pushStartElement(events, XSLTKeys.XSLT_NS, "value-of", "xsl:value-of", a);
        pushEndElement(events, XSLTKeys.XSLT_NS, "value-of", "xsl:value-of");
        return events;
    }

    private String tagToString(String qName, Attributes atts) {
        StringBuffer msg = new StringBuffer(10);
        msg.append("<").append(qName);
        for (int i=0; i < atts.getLength(); i++) {
            msg.append(" ").append(atts.getQName(i));
            msg.append("=\"").append(atts.getValue(i)).append("\"");
        }
        msg.append(">");
        return msg.toString();
    }

    private String escapeSingleQuot(String str) {
        return StringUtil.replaceCharByEntityRef(str, '\'');
    }

    private boolean isAugmentationAllowed() {
        return withinTemplate && !withinXslText;
    }

    private boolean isAugmented(String localName) {
        return isAugmentationAllowed() && !NOT_AUGMENTED_STATEMENTS.contains(localName);
    }

    private boolean isAugmentedAfterStart(String localName) {
        return AUGMENTED_AFTER_START.contains(localName);
    }

    private static final Set AUGMENTED_AFTER_START = new HashSet();
    static {
        AUGMENTED_AFTER_START.add("for-each");
        AUGMENTED_AFTER_START.add("for-each-group");
        AUGMENTED_AFTER_START.add("otherwise");
        AUGMENTED_AFTER_START.add("template");
        AUGMENTED_AFTER_START.add("when");
    }

    private static final Set NOT_AUGMENTED_STATEMENTS = new HashSet();
    static {
        NOT_AUGMENTED_STATEMENTS.add("with-param");
        NOT_AUGMENTED_STATEMENTS.add("param");
    }

    interface Event {
        void generate() throws SAXException;
    }

    class StartElementEvent implements Event {
        private String uri;
        private String localName;
        private String qName;
        private Attributes atts;

        public StartElementEvent(String uri, String localName, String qName, Attributes atts) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
            this.atts = atts;
        }

        public void generate() throws SAXException {
            superStartElement(uri, localName, qName, atts);
        }
    }

    class EndElementEvent implements Event {
        private String uri;
        private String localName;
        private String qName;

        public EndElementEvent(String uri, String localName, String qName) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
        }

        public void generate() throws SAXException {
            superEndElement(uri, localName, qName);
        }
    }

    class CharactersEvent implements Event {
        private char[] chars;
        private int start;
        private int length;
        private int startLine;
        private int currentLevel;

        public CharactersEvent(char ch[], int start, int length) {
            this.chars = new char[length];
            System.arraycopy(ch, start, chars, 0, length);
            this.start = 0;
            this.length = length;
            this.startLine = locator.getLineNumber();
            this.currentLevel = level;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer(20);
            for (int i=start; i<start + length; i++) {
                buf.append(chars[i]);
            }

            return buf.toString();
        }

        public int getStartLineNum() {
            return startLine;
        }

        public int getLevel() {
            return currentLevel;
        }

        public void generate() throws SAXException {
            superCharacters(chars, start, length);
        }
    }

    class PiEvent implements Event {
        private String target;
        private String data;
        private int startLine;
        private int currentLevel;

        public PiEvent(String target, String data) {
            this.target = target;
            this.data = data;
            this.startLine = locator.getLineNumber();
            this.currentLevel = level;
        }

        public int getStartLineNum() {
            return startLine;
        }

        public int getLevel() {
            return currentLevel;
        }

        public void generate() throws SAXException {
            superProcessingInstruction(target, data);
        }
    }
}
