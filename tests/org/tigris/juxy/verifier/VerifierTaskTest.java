package org.tigris.juxy.verifier;

import org.apache.tools.ant.BuildFileTest;

/**
 * $Id: VerifierTaskTest.java,v 1.1 2005-08-30 19:51:19 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class VerifierTaskTest extends BuildFileTest {
    public VerifierTaskTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        configureProject("tests/xml/build.xml");
    }

    public void testVerifierTask() {
        executeTarget("verify");
        System.out.println(getLog());
    }
}
