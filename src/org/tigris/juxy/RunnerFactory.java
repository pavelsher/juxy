package org.tigris.juxy;

/**
 * The factory used for Runner objects creating.
 *
 * @version $Revision: 1.1 $
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
