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
 * $Id: TracingFilter.java,v 1.2 2005-08-17 18:21:29 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class TracingFilter extends XMLFilterImpl {
    private final static Log logger = LogFactory.getLog(TracingFilter.class);
    private Locator locator;
    private int level = 0;
    private boolean withinTemplate;

    private List activeQueue = new ArrayList(3);
    private List postponedQueue = new ArrayList(3);

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

    private void pushStartElement(List queue, String uri, String localName, String qName, Attributes atts) {
        queue.add(new StartElementEvent(uri, localName, qName, atts));
    }

    private void pushEndElement(List queue, String uri, String localName, String qName) {
        queue.add(new EndElementEvent(uri, localName, qName));
    }

    private void pushEvents(List queue, List events) {
        queue.addAll(events);
    }

    private void flushActiveQueue() throws SAXException {
        flushQueue(activeQueue);
    }

    private void flushPostponedQueue() throws SAXException {
        flushQueue(postponedQueue);
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
                pushEvents(postponedQueue, generateValueOfEvents(tagToString(qName, atts)));
                pushStartElement(activeQueue, uri, localName, qName, atts);
            } else {
                if (!isParamElement(localName))
                    flushPostponedQueue();

                String tag = tagToString(qName, atts);
                if (isAugmentationPossible(localName) && !isAugmentedAfterStart(localName))
                    pushEvents(activeQueue, generateValueOfEvents(tag));

                pushStartElement(activeQueue, uri, localName, qName, atts);

                if (isAugmentationPossible(localName) && isAugmentedAfterStart(localName))
                    pushEvents(activeQueue, generateValueOfEvents(tag));
            }
        }

        flushActiveQueue();
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
        level--;
        if (isTemplateElement(uri, localName)) {
            withinTemplate = false;
            flushPostponedQueue();
        }

        super.endElement(uri, localName, qName);
    }

    private List generateValueOfEvents(String tag) {
        List events = new ArrayList(2);
        AttributesImpl a = new AttributesImpl();
        a.addAttribute("", "select", "select", "CDATA",
                "tracer:trace($" + JuxyParams.PREFIX + ":" + JuxyParams.TRACE_PARAM + ", " +
                            locator.getLineNumber() + ", " +
                            level + ", '" +
                            escapeSingleQuot(locator.getSystemId()) + "', '" +
                            escapeSingleQuot(tag) + "')");

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

    private boolean isAugmentationPossible(String localName) {
        return withinTemplate && !NOT_AUGMENTED_STATEMENTS.contains(localName);
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
}
