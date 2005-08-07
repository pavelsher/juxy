package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * $Id: VariableBase.java,v 1.4 2005-08-07 17:29:55 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class VariableBase
{
    private final String qname;
    private final VariableValueContainer value;

    VariableBase(String qname, String value)
    {
        ArgumentAssert.notEmpty(qname, "Name must not be empty");

        this.qname = qname.trim();
        this.value = new VariableValueContainer(value);
    }

    VariableBase(String qname, XPathExpr xpath)
    {
        ArgumentAssert.notEmpty(qname, "Name must not be empty");

        this.qname = qname.trim();
        this.value = new VariableValueContainer(xpath);
    }

    VariableBase(String qname, Document content)
    {
        ArgumentAssert.notEmpty(qname, "Name must not be empty");

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
