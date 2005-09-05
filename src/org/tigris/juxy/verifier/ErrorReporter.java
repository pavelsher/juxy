package org.tigris.juxy.verifier;

/**
 */
public interface ErrorReporter {
    void log(String message);

    void error(String message);

    void warning(String message);
}
