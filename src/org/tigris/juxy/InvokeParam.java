package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * $Id: InvokeParam.java,v 1.5 2005-08-10 08:57:18 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class InvokeParam extends VariableBase
{
    public InvokeParam(String qname, String value)
    {
        super(qname, value);
    }

    public InvokeParam(String qname, XPathExpr xpath)
    {
        super(qname, xpath);
    }

    public InvokeParam(String qname, Document content)
    {
        super(qname, content);
    }
}
