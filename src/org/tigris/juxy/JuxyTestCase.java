package org.tigris.juxy;

import junit.framework.TestCase;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.util.XMLComparator;
import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Node;

import javax.xml.transform.URIResolver;

/**
 * $Id: JuxyTestCase.java,v 1.6 2005-08-07 16:43:16 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public abstract class JuxyTestCase extends TestCase {
    private Runner runner;
    private RunnerContext context;

    /**
     * Creates new RunnerContext from the specified system id
     * @param systemId stylesheet system id
     * @return new RunnerContext
     */
    public RunnerContext newContext(String systemId) {
        context = getRunner().newRunnerContext(systemId);
        return context;
    }

    /**
     * Creates new RunnerContext from the system id. Uses specified
     * resolver to resolve system id.
     * @param systemId stylesheet system id
     * @param resolver URIResolver to use for system id resolution during transformation
     * @return new RunnerContext
     */
    public RunnerContext newContext(String systemId, URIResolver resolver) {
        context = getRunner().newRunnerContext(systemId, resolver);
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
     * Sets RunnerContext object to use as the current context.
     * @param context
     */
    public void setContext(RunnerContext context) {
        ArgumentAssert.notNull(context, "Context must not be null");
        this.context = context;
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
    public Node applyTemplates(XPathExpr xpath) throws Exception {
        return getRunner().applyTemplates(getContext(), xpath);
    }

    /**
     * For method description see {@link Runner#applyTemplates(RunnerContext, org.tigris.juxy.xpath.XPathExpr, String)}
     */
    public Node applyTemplates(XPathExpr xpath, String mode) throws Exception {
        return getRunner().applyTemplates(getContext(), xpath, mode);
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
