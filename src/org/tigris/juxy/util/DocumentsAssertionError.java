package org.tigris.juxy.util;

import junit.framework.AssertionFailedError;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.TreeWalker;

import java.util.Arrays;

/**
 * $Id: DocumentsAssertionError.java,v 1.6 2005-09-12 07:43:47 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class DocumentsAssertionError extends AssertionFailedError {
    private TreeWalker etw;
    private TreeWalker atw;
    private String message;

    public DocumentsAssertionError(TreeWalker etw, TreeWalker atw) {
        this.etw = etw;
        this.atw = atw;
        init();
    }

    private void init() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("Documents differ, expected document:\n");
        appendDocument(buf, etw);
        buf.append("\nActual document:\n");
        appendDocument(buf, atw);
        message = buf.toString();
    }

    public String getMessage() {
        return message;
    }

    private void appendDocument(StringBuffer buf, TreeWalker tw) {
        Node startFrom = tw.getCurrentNode();
        if (hasParentElement(startFrom))
            startFrom = tw.parentNode();
        
        if (startFrom.getOwnerDocument() == null)
            startFrom = tw.nextNode();

        String delimiter = null;
        if (hasParentElement(startFrom))
            delimiter = "...";

        appendDelimiter(buf, delimiter);
        serialize(buf, tw, 0);
        appendDelimiter(buf, delimiter);
    }

    private boolean hasParentElement(Node startFrom)
    {
        if (startFrom.getNodeType() == Node.DOCUMENT_NODE)
            return false;
        
        return startFrom.getParentNode() != null && startFrom.getParentNode().getNodeType() == Node.ELEMENT_NODE;
    }

    private void appendDelimiter(StringBuffer buf, String delimiter) {
        if (delimiter != null) {
            appendNewLine(buf);
            buf.append(delimiter).append('\n');
        }
    }

    private void serialize(StringBuffer buf, TreeWalker tw, int level) {
        char[] prefixChars = new char[level * 2];
        Arrays.fill(prefixChars, ' ');

        Node currentNode = tw.getCurrentNode();
        while (currentNode != null) {
            switch(currentNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    appendNewLine(buf);
                    appendPrefix(buf, prefixChars).append("<").append(currentNode.getNodeName());
                    NamedNodeMap attrs = currentNode.getAttributes();
                    for (int i=0; i<attrs.getLength(); i++) {
                        Node attr = attrs.item(i);
                        // we will skip here xmlns attribute defining namespace of currentNode element
                        // and write it later
                        if (attr.getNodeName().startsWith("xmlns") && attr.getNodeValue().equals(currentNode.getNamespaceURI())) continue;
                        buf.append(" ").append(attr.getNodeName()).append("=\"").append(getAttributeValue(attr)).append("\"");
                    }

                    if (currentNode.getNamespaceURI() != null) {
                        buf.append(" xmlns");
                        if (currentNode.getPrefix() != null)
                            buf.append(':').append(currentNode.getPrefix());
                        buf.append("=\"").append(currentNode.getNamespaceURI()).append("\"");
                    }

                    Node child = tw.firstChild();
                    if (child == null) {
                        buf.append("/>\n");
                    }
                    else {
                        buf.append(">");
                        serialize(buf, tw, level + 1);
                        appendPrefix(buf, prefixChars).append("</").append(currentNode.getNodeName()).append(">\n");
                    }
                    tw.setCurrentNode(currentNode);
                    break;
                case Node.TEXT_NODE:
                    String val = currentNode.getNodeValue().trim();
                    if (val.length() > 0)
                        buf.append(StringUtil.escapeXMLText(val));
                    break;
                case Node.COMMENT_NODE:
                    appendPrefix(buf, prefixChars).append("<!--").append(currentNode.getNodeValue()).append("-->");
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    appendPrefix(buf, prefixChars).append("<?").append(currentNode.getNodeName()).append(' ').append(currentNode.getNodeValue()).append("?>");
                    break;
                case Node.CDATA_SECTION_NODE:
                    appendPrefix(buf, prefixChars).append("<![CDATA[").append(currentNode.getNodeValue()).append("]]>");
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    DocumentType dt = (DocumentType) currentNode;
                    buf.append("<!DOCTYPE ").append(currentNode.getNodeName());
                    if (dt.getPublicId() != null) {
                        buf.append(" PUBLIC \"").append(dt.getPublicId()).append("\"");
                        buf.append(" \"").append(dt.getSystemId()).append("\"");
                    } else if (dt.getSystemId() != null) {
                        buf.append(" SYSTEM \"").append(dt.getSystemId()).append("\"");
                    }
                    if (dt.getInternalSubset() != null) {
                        buf.append(" [\n");
                        buf.append(dt.getInternalSubset());
                        buf.append("]");
                    }
                    buf.append(">");
                    break;
            }

            currentNode = tw.nextSibling();
        }
    }

    private String getAttributeValue(Node attr) {
        return StringUtil.escapeQuoteCharacter(StringUtil.escapeXMLText(attr.getNodeValue()));
    }

    private StringBuffer appendNewLine(StringBuffer buf) {
        if (!endsWithNewLine(buf))
            buf.append('\n');
        return buf;
    }

    private boolean endsWithNewLine(StringBuffer buf)
    {
        return buf.charAt(buf.length() - 1) == '\n';
    }

    private StringBuffer appendPrefix(StringBuffer buf, char[] prefix) {
        if (endsWithNewLine(buf))
            buf.append(prefix);
        return buf;
    }
}
