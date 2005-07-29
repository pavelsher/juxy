package org.tigris.juxy.util;

import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;

import java.util.Arrays;

import junit.framework.AssertionFailedError;

/**
 * $Id: DocumentsAssertionError.java,v 1.1 2005-07-29 17:43:43 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class DocumentsAssertionError extends AssertionFailedError {
    private TreeWalker etw;
    private TreeWalker atw;

    public DocumentsAssertionError(TreeWalker etw, TreeWalker atw) {
        this.etw = etw;
        this.atw = atw;
    }

    public String getMessage() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("Documents differ, expected document:");
        appendDocument(buf, etw);
        buf.append("\nActual document:");
        appendDocument(buf, atw);
        return buf.toString();
    }

    private void appendDocument(StringBuffer buf, TreeWalker tw) {
        Node parent = tw.parentNode();
        if (parent == null) {
            buf.append("\n");
            return;
        }

        if (parent.getOwnerDocument() == null) parent = tw.nextNode();

        String fragdelim = parent.getNodeType() != Node.DOCUMENT_TYPE_NODE
                && parent != parent.getOwnerDocument().getDocumentElement() ? "\n...\n" : "\n";

        buf.append(fragdelim);
        serialize(buf, tw, 0);
        buf.append(fragdelim);
    }

    private void serialize(StringBuffer buf, TreeWalker tw, int level) {
        char[] prefixChars = new char[level * 2];
        Arrays.fill(prefixChars, ' ');

        Node currentNode = tw.getCurrentNode();
        if (currentNode != null) {
            switch(currentNode.getNodeType()) {
                case Node.DOCUMENT_FRAGMENT_NODE:
                    tw.nextNode();
                    break;
                case Node.ELEMENT_NODE:
                    appendNewLineIfNeeded(buf);
                    buf.append(prefixChars);
                    buf.append("<").append(currentNode.getNodeName());
                    NamedNodeMap attrs = currentNode.getAttributes();
                    for (int i=0; i<attrs.getLength(); i++) {
                        Node attr = attrs.item(i);
                        // we will skip here xmlns attribute defining namespace of currentNode element
                        // and write it later
                        if ("xmlns".equals(attr.getPrefix()) && attr.getLocalName().equals(currentNode.getPrefix())) continue;
                        buf.append(" ").append(attr.getNodeName()).append("=\"").append(getAttributeValue(attr)).append("\"");
                    }

                    if (currentNode.getPrefix() != null) {
                        buf.append(" xmlns:").append(currentNode.getPrefix()).append("=\"").append(currentNode.getNamespaceURI()).append("\"");
                    }

                    Node child = tw.firstChild();
                    if (child == null) {
                        buf.append("/>");
                    }
                    else {
                        buf.append(">\n");
                        serialize(buf, tw, level + 1);
                        appendNewLineIfNeeded(buf);
                        buf.append(prefixChars).append("</").append(currentNode.getNodeName()).append(">");
                    }
                    break;
                case Node.TEXT_NODE:
                    String val = currentNode.getNodeValue().trim();
                    if (val.length() > 0)
                        buf.append(prefixChars).append(StringUtil.escapeXMLText(val));
                    break;
                case Node.COMMENT_NODE:
                    buf.append(prefixChars).append("<!--").append(currentNode.getNodeValue()).append("-->");
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    buf.append(prefixChars).append("<?").append(currentNode.getNodeName()).append(' ').append(currentNode.getNodeValue()).append("?>");
                    break;
                case Node.CDATA_SECTION_NODE:
                    buf.append(prefixChars).append("<![CDATA[").append(currentNode.getNodeValue()).append("]]>");
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
                case Node.ENTITY_NODE:
                case Node.ENTITY_REFERENCE_NODE:
                case Node.NOTATION_NODE:
                    // parsed entities will be automatically expanded,
                    // notations are not supported
                    break;
            }

            Node sibling = tw.nextSibling();
            if (sibling != null)
                serialize(buf, tw, level);
        }
    }

    private String getAttributeValue(Node attr) {
        return StringUtil.escapeQuoteCharacter(StringUtil.escapeXMLText(attr.getNodeValue()));
    }

    private void appendNewLineIfNeeded(StringBuffer buf) {
        if (buf.charAt(buf.length() - 1) != '\n') buf.append('\n');
    }
}
