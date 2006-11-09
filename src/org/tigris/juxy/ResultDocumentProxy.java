package org.tigris.juxy;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.traversal.NodeFilter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * A proxy for any empty org.w3c.dom.Document object which behaves like if Document
 * has all the nodes from the specified DocumentFragment as its direct children.
 * I.e. this proxy will append all DocumentFragment child nodes to the Document itself
 * thus avoiding the constraint that Document object must have only one child node.
 */
public class ResultDocumentProxy implements InvocationHandler {
  private Document adaptedDocument;
  private DocumentFragment fragment;

  private ResultDocumentProxy(Document adaptedDocument, DocumentFragment fragment) {
    this.adaptedDocument = adaptedDocument;
    this.fragment = fragment;
  }

  public static Object newInstance(Document adaptedDocument, DocumentFragment fragment) {
    return java.lang.reflect.Proxy.newProxyInstance(
        adaptedDocument.getClass().getClassLoader(),
        new Class[]{Document.class, DocumentTraversal.class},
        new ResultDocumentProxy(adaptedDocument, fragment));
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    //noinspection EmptyCatchBlock
    try {
      Method proxiedMethod = ResultDocumentProxy.class.getMethod(method.getName(), method.getParameterTypes());
      return proxiedMethod.invoke(this, args);
    } catch (NoSuchMethodException e) {
    }

    return method.invoke(adaptedDocument, args);
  }

  public NodeList getChildNodes() {
    return fragment.getChildNodes();
  }

  public Node getFirstChild() {
    return fragment.getFirstChild();
  }

  public Node getLastChild() {
    return fragment.getLastChild();
  }

  public boolean hasChildNodes() {
    return fragment.hasChildNodes();
  }

  public TreeWalker createTreeWalker(Node node, int i, NodeFilter filter, boolean b) throws org.w3c.dom.DOMException {
    // if passed node is Document node then we will substituite it with actual document
    Node startNode = node instanceof Document ? adaptedDocument : node;
    return ((DocumentTraversal)adaptedDocument).createTreeWalker(startNode, i, filter, b);
  }
}
