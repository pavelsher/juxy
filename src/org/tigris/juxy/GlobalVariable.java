package org.tigris.juxy;

import org.w3c.dom.Document;
import org.tigris.juxy.xpath.XPathExpr;

/**
 * $Id: GlobalVariable.java,v 1.4 2005-08-07 17:29:55 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class GlobalVariable extends VariableBase
{
    public GlobalVariable(final String qname, final String value)
    {
        super(qname, value);
    }

    public GlobalVariable(final String qname, final XPathExpr xpath)
    {
        super(qname, xpath);
    }

    public GlobalVariable(final String qname, final Document content)
    {
        super(qname, content);
    }
}
