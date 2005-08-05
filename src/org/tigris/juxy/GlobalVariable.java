package org.tigris.juxy;

import org.w3c.dom.Document;
import org.tigris.juxy.xpath.XPathExpr;

/**
 * $Id: GlobalVariable.java,v 1.2 2005-08-05 08:31:11 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class GlobalVariable extends VariableBase
{
    public GlobalVariable(final String qname, final String value)
    {
        super(qname, value);
    }

    public GlobalVariable(final String qname, final XPathExpr selectXpathExpr)
    {
        super(qname, selectXpathExpr);
    }

    public GlobalVariable(final String qname, final Document content)
    {
        super(qname, content);
    }
}
