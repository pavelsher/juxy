package org.tigris.juxy.util;

/**
 * @version $Revision: 1.1 $
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
