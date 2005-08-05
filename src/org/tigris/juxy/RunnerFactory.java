package org.tigris.juxy;

/**
 * $Id: RunnerFactory.java,v 1.2 2005-08-05 08:31:11 pavelsher Exp $
 *
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
