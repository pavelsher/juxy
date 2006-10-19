package org.tigris.juxy.verifier;

import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.BuildException;
import org.apache.xml.resolver.CatalogManager;

/**
 * $Id: UTestVerifierTask.java,v 1.5 2006-10-19 07:23:23 pavelsher Exp $
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
        expectLogContaining("verification-nofiles", "0 stylesheet(s) were selected for verification");
    }

    public void testSpecifiedDirDoesNotExist() {
        expectBuildException("verification-invaliddir", "");
    }

    public void testNoAttributesAtAll() {
        expectBuildException("verification-noattributes", "");
    }

    public void testDirAndFilesetSpecified() {
        expectBuildException("verification-dirandfileset", "");
    }

    public void testVerificationSuccessful() {
        expectLogContaining("successful-verification", "1 stylesheet(s) were selected for verification");
    }

    public void testFailOnError() {
        try {
            executeTarget("failonerror");
            fail("An exception expected");
        } catch (BuildException e) {}

        assertTrue(getLog().contains("ERROR: Failed to parse file"));
        assertTrue(getLog().contains("not-well-formed.xsl"));
        assertFalse(getLog().contains("root.xsl"));
    }

    public void testNoFailOnError() {
        executeTarget("no-failonerror");

        assertTrue(getLog().contains("ERROR: Failed to parse file"));
        assertTrue(getLog().contains("not-well-formed.xsl"));
        assertTrue(getLog().contains("1 stylesheet(s) were selected for verification"));
        assertTrue(getLog().contains("root.xsl"));
    }

    public void testFactory() {
        expectBuildException("verification-factorywithoutname", "");
        expectBuildException("verification-factorywithemptyname", "");

        executeTarget("verification-validfactory");
        assertTrue(getLog().contains("Obtained TransformerFactory: net.sf.saxon.TransformerFactoryImpl"));
        assertTrue(getLog().contains("Obtained TransformerFactory: org.apache.xalan.processor.TransformerFactoryImpl"));
    }

    public void testCatalog() {
        expectBuildException("verification-emptycatalog", "");
        expectBuildException("verification-catalogwithemptycatalogfiles", "");

        executeTarget("verification-validcatalog");
    }

    protected void tearDown() throws Exception {
        System.out.println(getLog());
        // reset catalog files
        CatalogManager.getStaticManager().setCatalogFiles(null);
    }
}
