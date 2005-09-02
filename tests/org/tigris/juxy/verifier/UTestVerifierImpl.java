package org.tigris.juxy.verifier;

import junit.framework.TestCase;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class UTestVerifierImpl extends TestCase {
    private Verifier verifier;
    private DummyReporter reporter;

    protected void setUp() throws Exception {
        verifier = new VerifierImpl();
        reporter = new DummyReporter();
        verifier.setErrorReporter(reporter);
    }

    public void testOneSimpleStylesheet() {
        verifier.setFiles(files(new String[] {"no-imports.xsl"}));
        assertTrue(verifier.verify(false));
        assertEquals(1, verifier.getNumberOfVerifiedFiles());
    }

    public void testImportedStylesheetsUseGlobalVariable() {
        verifier.setFiles(files(new String[] {"imported.xsl", "included.xsl", "root.xsl"}));
        assertTrue(verifier.verify(false));
        assertEquals(1, verifier.getNumberOfVerifiedFiles());
    }

    public void testImportUsingURIResolver() {
        verifier.setFiles(files(new String[] {"imported.xsl", "resolver.xsl"}));
        verifier.setURIResolver(new URIResolver() {
            public Source resolve(String href, String base) {
                if ("some:uri".equals(href))
                    return new StreamSource(new File("tests/xml/verifier/imported.xsl"));

                return null;
            }
        });
        assertTrue(verifier.verify(false));
        assertEquals(1, verifier.getNumberOfVerifiedFiles());
    }

    public void testURIResolverThrowsException() {
        verifier.setFiles(files(new String[] {"resolver.xsl"}));
        verifier.setURIResolver(new URIResolver() {
            public Source resolve(String href, String base) throws TransformerException {
                throw new TransformerException("an exception");
            }
        });
        assertFalse(verifier.verify(false));
        assertEquals(0, verifier.getNumberOfVerifiedFiles());
        assertTrue(reporter.errors()[0].endsWith("an exception"));
    }

    public void testNotExistentFileIgnored() {
        verifier.setFiles(files(new String[] {"non-existent-file.xsl", "root.xsl"}));
        assertTrue(verifier.verify(false));
        assertEquals(1, verifier.getNumberOfVerifiedFiles());
    }

    public void testDirectoryInsteadOfFileIgnored() {
        verifier.setFiles(files(new String[] {".", "root.xsl"}));
        assertTrue(verifier.verify(false));
        assertEquals(1, verifier.getNumberOfVerifiedFiles());
    }

    public void testNotWellFormedFile() {
        verifier.setFiles(files(new String[] {"not-well-formed.xsl", "root.xsl"}));
        assertFalse(verifier.verify(false));
        assertEquals(1, verifier.getNumberOfVerifiedFiles());
        assertEquals(2, reporter.errors().length);
    }

    public void testNotWellFormedFileFailFast() {
        verifier.setFiles(files(new String[] {"not-well-formed.xsl", "root.xsl"}));
        assertFalse(verifier.verify(true));
        assertEquals(0, verifier.getNumberOfVerifiedFiles());
    }

    public void testStylesheetIsIncorrect() {
        verifier.setFiles(files(new String[] {"bad-stylesheet.xsl"}));
        assertFalse(verifier.verify(false));
        assertEquals(0, verifier.getNumberOfVerifiedFiles());
        assertTrue(reporter.errors().length > 0);
    }

    private List files(String[] paths) {
        List files = new ArrayList();
        for (int i=0; i<paths.length; i++) {
            files.add(new File("tests/xml/verifier/", paths[i]));
        }

        return files;
    }

    class DummyReporter implements ErrorReporter {
        private List errors = new ArrayList();

        public void debug(String message) {
            System.out.println("[DEBUG] " + message);
        }

        public void error(String message) {
            System.out.println("[ERROR] " + message);
            errors.add(message);
        }

        public void warning(String message) {
            System.out.println("[WARN] " + message);
        }

        public String[] errors() {
            return (String[]) errors.toArray(new String[] {});
        }
    }
}
