package org.tigris.juxy;

/**
 * <p/>
 * Factory of Runner objects.
 *
 * @author Pavel Sher
 */
public class RunnerFactory {
  /**
   * Creates new Runner.
   *
   * @return Runner object
   */
  public static Runner newRunner() {
    return new RunnerImpl();
  }
}
