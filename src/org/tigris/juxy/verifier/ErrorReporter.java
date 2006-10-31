package org.tigris.juxy.verifier;

/**
 */
public interface ErrorReporter {

  void info(String message);

  void error(String message);

  void warning(String message);
}
