package org.tigris.juxy;

import org.tigris.juxy.util.XMLComparator;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.xpath.XPathExpr;
import junit.framework.TestCase;
import org.w3c.dom.Node;

import java.io.FileNotFoundException;

/**
 * $Id: JuxyTestCase.java,v 1.1 2005-07-29 17:43:43 pavelsher Exp $
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
        context = getRunner().newInvokationContext(xslFile);
        return context;
    }

    public XPathExpr xpath(String xpathExpr) throws Exception {
        return new XPathExpr(xpathExpr);
    }

    public Node applyTemplates() throws Exception {
        return getRunner().applyTemplates(getContext());
    }

    public Node applyTemplates(XPathExpr selectNodeXpath) throws Exception {
        return getRunner().applyTemplates(getContext(), selectNodeXpath);
    }

    public Node applyTemplates(XPathExpr selectNodeXpath, String mode) throws Exception {
        return getRunner().applyTemplates(getContext(), selectNodeXpath, mode);
    }

    public Node callTemplate(String name) throws Exception {
        return getRunner().callTemplate(getContext(), name);
    }

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
