package org.tigris.juxy;

import org.tigris.juxy.util.XMLComparator;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.xpath.XPathExpr;
import junit.framework.TestCase;
import org.w3c.dom.Node;

import java.io.FileNotFoundException;

/**
 * $Id: JuxyTestCase.java,v 1.4 2005-08-05 08:31:11 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public abstract class JuxyTestCase extends TestCase {
    private Runner runner;
    private RunnerContext context;

    /**
     * Creates new RunnerContext from the specified xsl file
     * @param xslFile
     * @return new InvokationContext
     * @throws FileNotFoundException
     */
    public RunnerContext newContext(String xslFile) throws Exception {
        context = getRunner().newRunnerContext(xslFile);
        return context;
    }

    /**
     * Returns current RunnerContext object.
     * @return current RunnerContext object
     */
    public RunnerContext context() {
        if (context == null)
            throw new IllegalStateException("Call newContext() method first");
        return context;
    }

    /**
     * Creates new XPathExpr object.
     * @param xpathExpr an XPath expression
     * @return new XPathExpr object
     * @throws Exception
     */
    public XPathExpr xpath(String xpathExpr) throws Exception {
        return new XPathExpr(xpathExpr);
    }

    /**
     * For method description see {@link Runner#applyTemplates(RunnerContext)}
     */
    public Node applyTemplates() throws Exception {
        return getRunner().applyTemplates(getContext());
    }

    /**
     * For method description see {@link Runner#applyTemplates(RunnerContext, org.tigris.juxy.xpath.XPathExpr)}
     */
    public Node applyTemplates(XPathExpr selectNodeXpath) throws Exception {
        return getRunner().applyTemplates(getContext(), selectNodeXpath);
    }

    /**
     * For method description see {@link Runner#applyTemplates(RunnerContext, org.tigris.juxy.xpath.XPathExpr, String)}
     */
    public Node applyTemplates(XPathExpr selectNodeXpath, String mode) throws Exception {
        return getRunner().applyTemplates(getContext(), selectNodeXpath, mode);
    }

    /**
     * For method description see {@link Runner#callTemplate(RunnerContext, String)}
     */
    public Node callTemplate(String name) throws Exception {
        return getRunner().callTemplate(getContext(), name);
    }

    /**
     * Asserts that two documents are equal. Meaningless spaces will be ignored during this assertion.
     * @param expectedDocument XML document which is expected
     * @param actual document root node of actual transformation result
     */
    public static void assertXMLEquals(String expectedDocument, Node actual) throws Exception {
        XMLComparator.assertEquals(expectedDocument, actual);
    }

    /**
     * For method description see {@link StringUtil#normalizeSpaces(String)}
     * @param str
     * @return normalized string
     */
    public String normalizeSpaces(String str) {
        return StringUtil.normalizeSpaces(str);
    }

    /**
     * For method description see {@link StringUtil#normalizeAll(String)}
     * @param str
     * @return normalized string
     */
    public String normalizeAll(String str) {
        return StringUtil.normalizeAll(str);
    }

    private RunnerContext getContext() {
        if (context == null)
            throw new IllegalStateException("Call newContext method first");
        return context;
    }

    private Runner getRunner() {
        if (runner == null)
            runner = RunnerFactory.newRunner();
        return runner;
    }
}
