package org.tigris.juxy;

/**
 * $Id: RunnerFactory.java,v 1.3 2005-08-05 08:38:29 pavelsher Exp $
 * <p/>
 * The factory used for Runner objects creating.
 *
 * @author Pavel Sher
 */
public class RunnerFactory
{
    /**
     * Creates new Runner.
     * @return Runner object
     */
    public static Runner newRunner()
    {
        return new RunnerImpl();
    }
}
