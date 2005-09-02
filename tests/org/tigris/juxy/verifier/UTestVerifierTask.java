package org.tigris.juxy.verifier;

import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.BuildException;

/**
 * $Id: UTestVerifierTask.java,v 1.1 2005-09-02 08:19:53 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestVerifierTask extends BuildFileTest {
    public UTestVerifierTask(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        configureProject("tests/xml/verifier/build.xml");
    }

    public void testNoFilesFoundForVerification() {
        expectLogContaining("verification-nofiles", "Found 0 stylesheet(s) to verify");
    }

    public void testSpecifiedDirDoesNotExist() {
        try {
            executeTarget("verification-invaliddir");
            fail("An exception expected");
        } catch (BuildException e) {}
    }

    public void testNoAttributesAtAll() {
        try {
            executeTarget("verification-noattributes");
            fail("An exception expected");
        } catch (BuildException e) {}
    }

    public void testDirAndFilesetSpecified() {
        expectLogContaining("verification-dirandfileset", "Found 1 stylesheet(s) to verify");
    }

    public void testVerificationSuccessful() {
        expectLogContaining("successful-verification", "Found 1 stylesheet(s) to verify");
    }

    public void testFailFast() {
        try {
            executeTarget("failfast");
            fail("An exception expected");
        } catch (BuildException e) {}

        assertTrue(getLog().contains("ERROR: Failed to parse file"));
        assertTrue(getLog().contains("not-well-formed.xsl"));
        assertFalse(getLog().contains("root.xsl"));
    }

    public void testNoFailFast() {
        try {
            executeTarget("no-failfast");
            fail("An exception expected");
        } catch (BuildException e) {}

        assertTrue(getLog().contains("ERROR: Failed to parse file"));
        assertTrue(getLog().contains("not-well-formed.xsl"));
        assertTrue(getLog().contains("Found 1 stylesheet(s) to verify"));
        assertTrue(getLog().contains("root.xsl"));
    }

    protected void tearDown() throws Exception {
        System.out.println(getLog());
    }
}
