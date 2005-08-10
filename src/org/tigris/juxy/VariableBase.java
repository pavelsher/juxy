package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * $Id: VariableBase.java,v 1.5 2005-08-10 08:57:18 pavelsher Exp $
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariableBase)) return false;

        final VariableBase variableBase = (VariableBase) o;

        if (!qname.equals(variableBase.qname)) return false;
        if (!value.equals(variableBase.value)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = qname.hashCode();
        result = 29 * result + value.hashCode();
        return result;
    }
}
