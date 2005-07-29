package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 *
 * @version $Revision: 1.1 $
 * @author Pavel Sher
 */
public class VariableBase
{
    private final String qname;
    private final VariableValueContainer value;

    VariableBase(String qname, String value)
    {
        ArgumentAssert.notEmpty(qname, "The qname must not be empty");

        this.qname = qname.trim();
        this.value = new VariableValueContainer(value);
    }

    VariableBase(String qname, XPathExpr selectXpathExpr)
    {
        ArgumentAssert.notEmpty(qname, "The qname must not be empty");

        this.qname = qname.trim();
        this.value = new VariableValueContainer(selectXpathExpr);
    }

    VariableBase(String qname, Document content)
    {
        ArgumentAssert.notEmpty(qname, "The qname must not be empty");

        this.qname = qname.trim();
        this.value = new VariableValueContainer(content);
    }

    public boolean isVariableWithContent()
    {
        return value.isNotEmptyContent();
    }

    public boolean isXPathValue() {
        return value.isXPathValue();
    }

    public Document getContent()
    {
        if (isVariableWithContent())
            return value.getContent();

        return null;
    }

    public String getXPathValue() {
        return value.getXPathValue();
    }

    public String getStringValue()
    {
        return value.getStringValue();
    }

    public String getQname()
    {
        return qname;
    }
}
