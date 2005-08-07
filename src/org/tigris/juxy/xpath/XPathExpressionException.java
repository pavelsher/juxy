package org.tigris.juxy.xpath;

/**
 * $Id: XPathExpressionException.java,v 1.4 2005-08-07 16:43:15 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class XPathExpressionException extends Exception
{
    public XPathExpressionException(String message, Throwable rootCause)
    {
        super(message, rootCause);
   }
}
