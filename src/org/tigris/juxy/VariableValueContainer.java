package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * <p/>
 *
 * @author Pavel Sher
 */
class VariableValueContainer {
  private String stringValue = null;
  private XPathExpr selectXpathExpr = null;
  private Document variableContent = null;

  VariableValueContainer(String stringValue) {
    this.stringValue = stringValue;
  }

  VariableValueContainer(Document variableContent) {
    this.variableContent = variableContent;
  }

  VariableValueContainer(XPathExpr xpath) {
    this.selectXpathExpr = xpath;
  }

  public String getStringValue() {
    if (stringValue != null)
      return stringValue;

    return null;
  }

  public String getXPathValue() {
    if (selectXpathExpr != null)
      return selectXpathExpr.getExpression();

    return null;
  }

  public Document getContent() {
    return variableContent;
  }

  public boolean isNotEmptyContent() {
    return variableContent != null && variableContent.getDocumentElement() != null;
  }

  public boolean isXPathValue() {
    return selectXpathExpr != null;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof VariableValueContainer)) return false;

    final VariableValueContainer variableValueContainer = (VariableValueContainer) o;

    if (selectXpathExpr != null ? !selectXpathExpr.equals(variableValueContainer.selectXpathExpr) : variableValueContainer.selectXpathExpr != null)
      return false;
    if (stringValue != null ? !stringValue.equals(variableValueContainer.stringValue) : variableValueContainer.stringValue != null)
      return false;
    if (variableContent != null ? !variableContent.equals(variableValueContainer.variableContent) : variableValueContainer.variableContent != null)
      return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (stringValue != null ? stringValue.hashCode() : 0);
    result = 29 * result + (selectXpathExpr != null ? selectXpathExpr.hashCode() : 0);
    result = 29 * result + (variableContent != null ? variableContent.hashCode() : 0);
    return result;
  }
}
