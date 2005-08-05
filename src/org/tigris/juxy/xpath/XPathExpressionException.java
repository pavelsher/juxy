package org.tigris.juxy.xpath;

import java.io.PrintStream;

/**
 * $Id: XPathExpressionException.java,v 1.2 2005-08-05 08:31:11 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class XPathExpressionException extends Exception
{
    private Throwable rootCause = null;

    public XPathExpressionException(Throwable rootCause)
    {
        super();
        this.rootCause = rootCause;
    }

    public String getMessage()
    {
        return rootCause.getMessage();
    }

    public void printStackTrace()
    {
        rootCause.printStackTrace();
    }

    public void printStackTrace(PrintStream ps)
    {
        rootCause.printStackTrace(ps);
    }
}
