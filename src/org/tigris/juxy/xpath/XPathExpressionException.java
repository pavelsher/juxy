package org.tigris.juxy.xpath;

import java.io.PrintStream;

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
