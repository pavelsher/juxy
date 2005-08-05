package org.tigris.juxy.util;

/**
 * $Id: ArgumentAssert.java,v 1.2 2005-08-05 08:31:11 pavelsher Exp $
 *
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
