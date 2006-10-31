package org.tigris.juxy.util;

import org.w3c.dom.Node;

import java.util.Iterator;

/**
 * Iterator over DOM. This iterator iterates DOM elements in the
 * order of their appearance in the document: it first processess all child elements
 * down the DOM tree, then following sibling nodes on each level.
 * This is the same as iterate over results of the <code>//</code> XPath expression.
 *
 * @author Pavel Sher
 */
public class DOMIterator implements Iterator {
  private Node rootNode;
  private Node currentNode;

  private Node[] cache = new Node[]{null, null};

  public DOMIterator(Node rootNode) {
    if (rootNode == null)
      throw new IllegalArgumentException("rootNode must not be null");

    this.rootNode = rootNode;
    currentNode = rootNode;
  }

  /**
   * Not supported.
   */
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported");
  }

  public boolean hasNext() {
    if (currentNode.hasChildNodes())
      return true;

    return currentNode.getNextSibling() != null || getNodeWithSibling(currentNode) != null;
  }

  public Object next() {
    Node nextNode = getChildOrSiblingNode(currentNode);
    if (nextNode != null) {
      currentNode = nextNode;
      return currentNode;
    }

    // if there are no child and sibling nodes and current node is not
    // child of root node then move up
    Node nodeWithSibling = getNodeWithSibling(currentNode);
    if (nodeWithSibling != null) {
      currentNode = nodeWithSibling.getNextSibling();
      return currentNode;
    }

    return null;
  }

  /**
   * Traverses up to the root node and checks whether
   * current node has sibling nodes. Traverse stops when the first node
   * with siblings found or current node reaches rootNode.
   * Result of a search will be stored in the cache and if will be returned for all
   * subsequent callings with the same parameter.
   *
   * @param fromNode node from which traverse is performed
   * @return node having sibling nodes or null if such node was not found
   */
  private Node getNodeWithSibling(Node fromNode) {
    if (cache[0] == fromNode)
      return cache[1];

    if (fromNode == rootNode)
      return null;

    Node parent = fromNode.getParentNode();
    Node nodeWithSibling = null;
    while (parent != null && parent != rootNode) {
      if (parent.getNextSibling() != null) {
        nodeWithSibling = parent;
        break;
      }

      parent = parent.getParentNode();
    }

    cache[0] = currentNode;
    cache[1] = nodeWithSibling;

    return nodeWithSibling;
  }

  private Node getChildOrSiblingNode(Node fromNode) {
    // if current node has child nodes then return first child
    if (fromNode.hasChildNodes())
      return fromNode.getFirstChild();

    // if there are no child elements return first sibling element
    return fromNode.getNextSibling();
  }
}
