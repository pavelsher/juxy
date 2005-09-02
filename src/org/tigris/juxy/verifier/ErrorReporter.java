package org.tigris.juxy.verifier;

/**
 */
public interface ErrorReporter {
    void debug(String message);

    void error(String message);

    void warning(String message);
}
