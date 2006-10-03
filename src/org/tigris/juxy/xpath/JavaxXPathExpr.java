package org.tigris.juxy.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.tigris.juxy.util.ArgumentAssert;

import javax.xml.xpath.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.util.*;

/**
 * @author Pavel Sher
1 */
public class JavaxXPathExpr implements XPathExpr {
    private XPath xpath;
    private String expression;
    private Map namespaces;

    public JavaxXPathExpr(String expression) throws XPathFactoryConfigurationException {
        assert expression != null;

        this.xpath = javax.xml.xpath.XPathFactory.newInstance(javax.xml.xpath.XPathConstants.DOM_OBJECT_MODEL).newXPath();
        this.expression = expression;
        this.namespaces = new HashMap();
    }

    public XPathExpr addNamespace(String prefix, String uri) throws XPathExpressionException {
        ArgumentAssert.notNull(prefix, "Prefix must not be null");
        ArgumentAssert.notEmpty(uri, "URI must not be empty");
        namespaces.put(uri, prefix);
        return this;
    }

    public boolean toBoolean(Node node) throws XPathExpressionException {
        checkNode(node);
        Boolean result = (Boolean) evaluateXPath(node, XPathConstants.BOOLEAN);
        return result.booleanValue();
    }

    private void checkNode(Node node) {
        ArgumentAssert.notNull(node, "Node must not be null");
    }

    public String toString(Node node) throws XPathExpressionException {
        checkNode(node);
        return (String) evaluateXPath(node, XPathConstants.STRING);
    }

    public int toInt(Node node) throws XPathExpressionException {
        checkNode(node);
        Double result = (Double) evaluateXPath(node, XPathConstants.NUMBER);
        return result.intValue();
    }

    public double toDouble(Node node) throws XPathExpressionException {
        checkNode(node);
        Double result = (Double) evaluateXPath(node, XPathConstants.NUMBER);
        return result.doubleValue();
    }

    public List toNodeList(Node node) throws XPathExpressionException {
        checkNode(node);
        NodeList nodes = (NodeList) evaluateXPath(node, XPathConstants.NODESET);
        List nodeList = new ArrayList(nodes.getLength());
        for (int i=0; i<nodes.getLength(); i++)
            nodeList.add(nodes.item(i));
        return nodeList;
    }

    public Node toNode(Node node) throws XPathExpressionException {
        checkNode(node);
        return (Node) evaluateXPath(node, XPathConstants.NODE);
    }

    public String getExpression() {
        return expression;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaxXPathExpr)) return false;

        final JavaxXPathExpr java50XPathExpr = (JavaxXPathExpr) o;

        if (!expression.equals(java50XPathExpr.expression)) return false;

        return true;
    }

    public int hashCode() {
        return expression.hashCode();
    }

    private Object evaluateXPath(Node node, QName type) throws XPathExpressionException {
        Object result;
        try {
            prepareXPathNamespaces();
            result = xpath.evaluate(expression, node, type);
        } catch (javax.xml.xpath.XPathExpressionException e) {
            throw new XPathExpressionException("Failed to evaluate XPath expression: " + expression, e);
        }
        return result;
    }

    private void prepareXPathNamespaces() {
        xpath.setNamespaceContext(new JuxyNamespaceContext(namespaces));
    }

    class JuxyNamespaceContext implements NamespaceContext {
        private Map namespaces; // URI is a key, prefix is a value

        public JuxyNamespaceContext(Map namespaces) {
            this.namespaces = namespaces;
        }

        public String getNamespaceURI(String prefix) {
            Iterator nsIt = namespaces.entrySet().iterator();
            while(nsIt.hasNext()) {
                Map.Entry e = (Map.Entry)nsIt.next();
                String uri = (String) e.getKey();
                String pref = (String) e.getValue();
                if (prefix.equals(pref))
                    return uri;
            }

            return null;
        }

        public String getPrefix(String namespaceURI) {
            return (String) namespaces.get(namespaceURI);
        }

        public Iterator getPrefixes(String namespaceURI) {
            String prefix = getPrefix(namespaceURI);
            if (prefix == null)
                return Collections.emptyList().iterator();

            return Arrays.asList(new String[] {prefix}).iterator();
        }
    }
}
