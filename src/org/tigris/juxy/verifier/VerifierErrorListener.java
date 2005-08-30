package org.tigris.juxy.verifier;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.ArrayList;

/**
 */
public class VerifierErrorListener implements ErrorListener {
    private List collectedErrors = new ArrayList(5);
    private boolean wereErrors = false;

    public void warning(TransformerException exception) throws TransformerException {
        collectedErrors.add(new Error(exception, true));
    }

    public void error(TransformerException exception) throws TransformerException {
        collectedErrors.add(new Error(exception, false));
        wereErrors = true;
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        collectedErrors.add(new Error(exception, false));
        wereErrors = true;
    }

    public List getCollectedErrors() {
        return collectedErrors;
    }

    public boolean wereErrors() {
        return wereErrors;
    }

    public static class Error {
        private TransformerException exception;
        private boolean warning;

        public Error(TransformerException exception, boolean warning) {
            this.exception = exception;
            this.warning = warning;
        }

        public TransformerException getException() {
            return exception;
        }

        public boolean isWarning() {
            return warning;
        }
    }
}
