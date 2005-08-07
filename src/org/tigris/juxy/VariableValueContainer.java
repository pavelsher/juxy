package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * $Id: VariableValueContainer.java,v 1.4 2005-08-07 17:29:55 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
class VariableValueContainer
{
    private String stringValue = null;
    private XPathExpr selectXpathExpr = null;
    private Document variableContent = null;

    VariableValueContainer(String stringValue)
    {
        this.stringValue = stringValue;
    }

    VariableValueContainer(Document variableContent)
    {
        this.variableContent = variableContent;
    }

    VariableValueContainer(XPathExpr xpath)
    {
        this.selectXpathExpr = xpath;
    }

    public String getStringValue()
    {
        if (stringValue != null)
            return stringValue;

        return null;
    }

    public String getXPathValue() {
        if (selectXpathExpr != null)
            return selectXpathExpr.getExpression();

        return null;
    }

    public Document getContent()
    {
        return variableContent;
    }

    public boolean isNotEmptyContent()
    {
        return variableContent != null && variableContent.getDocumentElement() != null;
    }

    public boolean isXPathValue() {
        return selectXpathExpr != null;
    }
}
