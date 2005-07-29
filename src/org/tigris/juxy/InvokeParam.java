package org.tigris.juxy;

import org.w3c.dom.Document;
import org.tigris.juxy.xpath.XPathExpr;

/**
 *
 * @version $Revision: 1.1 $
 * @author Pavel Sher
 */
public class InvokeParam extends VariableBase
{
    public InvokeParam(String qname, String value)
    {
        super(qname, value);
    }

    public InvokeParam(String qname, XPathExpr selectXpathExpr)
    {
        super(qname, selectXpathExpr);
    }

    public InvokeParam(String qname, Document content)
    {
        super(qname, content);
    }
}