package org.tigris.juxy;

import org.w3c.dom.Document;
import org.tigris.juxy.xpath.XPathExpr;

/**
 * $Id: InvokeParam.java,v 1.4 2005-08-07 17:29:55 pavelsher Exp $
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
