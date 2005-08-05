package org.tigris.juxy.util;

/**
 * $Id: ArgumentAssert.java,v 1.3 2005-08-05 08:38:29 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class ArgumentAssert
{
    public static void notNull(Object obj, String message)
    {
        if (obj == null)
            throw new IllegalArgumentException(message);
    }

    public static void notEmpty(String value, String messsage)
    {
        if (value == null || value.trim().length() == 0)
            throw new IllegalArgumentException(messsage);
    }
}
