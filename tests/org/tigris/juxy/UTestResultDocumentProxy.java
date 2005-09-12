package org.tigris.juxy;

import junit.framework.TestCase;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.JavaxXPathExpr;
import org.tigris.juxy.xpath.JaxenXPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathFactoryConfigurationException;

/**
 */
public class UTestResultDocumentProxy extends TestCase {
    public void testTwoTextNodesInTheFragment() {
        Document doc = DOMUtil.newDocument();
        DocumentFragment fragment = doc.createDocumentFragment();
        fragment.appendChild(doc.createTextNode("text 1"));
        fragment.appendChild(doc.createTextNode("text 2"));

        Document proxiedDoc = (Document) ResultDocumentProxy.newInstance(doc, fragment);
        assertEquals("text 1", proxiedDoc.getChildNodes().item(0).getNodeValue());
        assertEquals("text 2", proxiedDoc.getChildNodes().item(1).getNodeValue());
    }

    public void testElementAndTextNode() {
        Document doc = DOMUtil.newDocument();
        DocumentFragment fragment = doc.createDocumentFragment();
        fragment.appendChild(doc.createElement("elem"));
        fragment.appendChild(doc.createTextNode("text 2"));

        Document proxiedDoc = (Document) ResultDocumentProxy.newInstance(doc, fragment);
        assertEquals("elem", proxiedDoc.getChildNodes().item(0).getNodeName());
        assertEquals(Node.ELEMENT_NODE, proxiedDoc.getChildNodes().item(0).getNodeType());
        assertEquals("text 2", proxiedDoc.getChildNodes().item(1).getNodeValue());
    }

    public void testTwoRootElements() {
        Document doc = DOMUtil.newDocument();
        DocumentFragment fragment = doc.createDocumentFragment();
        fragment.appendChild(doc.createElement("elem1"));
        fragment.appendChild(doc.createElement("elem2"));

        Document proxiedDoc = (Document) ResultDocumentProxy.newInstance(doc, fragment);
        assertEquals("elem1", proxiedDoc.getChildNodes().item(0).getNodeName());
        assertEquals("elem2", proxiedDoc.getChildNodes().item(1).getNodeName());
    }

    public void testJaxenXPathOverProxiedDocument() throws XPathExpressionException {
        Document doc = DOMUtil.newDocument();
        DocumentFragment fragment = doc.createDocumentFragment();
        fragment.appendChild(doc.createElement("elem1"));
        fragment.appendChild(doc.createElement("elem2"));

        Document proxiedDoc = (Document) ResultDocumentProxy.newInstance(doc, fragment);
        assertEquals(Node.DOCUMENT_NODE, new JaxenXPathExpr("/").toNode(proxiedDoc).getNodeType());

        JaxenXPathExpr x = new JaxenXPathExpr("/*");
        Element elem1 = (Element) x.toNodeList(proxiedDoc).get(0);
        Element elem2 = (Element) x.toNodeList(proxiedDoc).get(1);
        assertEquals("elem1", elem1.getNodeName());
        assertEquals("elem2", elem2.getNodeName());

        x = new JaxenXPathExpr("/*[1]");
        assertEquals("elem1", x.toNode(proxiedDoc).getNodeName());

        x = new JaxenXPathExpr("/*[last()]");
        assertEquals("elem2", x.toNode(proxiedDoc).getNodeName());

        x = new JaxenXPathExpr("/*[1]/following-sibling::*");
        assertEquals("elem2", x.toNode(proxiedDoc).getNodeName());

        x = new JaxenXPathExpr("/*[last()]/preceding-sibling::*");
        assertEquals("elem1", x.toNode(proxiedDoc).getNodeName());
    }

    public void testJavaxXPathOverProxiedDocument() throws XPathExpressionException, XPathFactoryConfigurationException {
        Document doc = DOMUtil.newDocument();
        DocumentFragment fragment = doc.createDocumentFragment();
        fragment.appendChild(doc.createElement("elem1"));
        fragment.appendChild(doc.createElement("elem2"));

        Document proxiedDoc = (Document) ResultDocumentProxy.newInstance(doc, fragment);
        assertEquals(Node.DOCUMENT_NODE, new JavaxXPathExpr("/").toNode(proxiedDoc).getNodeType());

        JavaxXPathExpr x = new JavaxXPathExpr("/*");
        Element elem1 = (Element) x.toNodeList(proxiedDoc).get(0);
        Element elem2 = (Element) x.toNodeList(proxiedDoc).get(1);
        assertEquals("elem1", elem1.getNodeName());
        assertEquals("elem2", elem2.getNodeName());

        x = new JavaxXPathExpr("/*[1]");
        assertEquals("elem1", x.toNode(proxiedDoc).getNodeName());

        x = new JavaxXPathExpr("/*[last()]");
        assertEquals("elem2", x.toNode(proxiedDoc).getNodeName());

        x = new JavaxXPathExpr("/*[1]/following-sibling::*");
        assertEquals("elem2", x.toNode(proxiedDoc).getNodeName());

        x = new JavaxXPathExpr("/*[last()]/preceding-sibling::*");
        assertEquals("elem1", x.toNode(proxiedDoc).getNodeName());
    }
}
