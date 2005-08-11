package org.tigris.juxy;

import junit.framework.TestCase;
import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.util.StringUtil;
import org.tigris.juxy.util.XMLComparator;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.URIResolver;
import java.io.ByteArrayOutputStream;

/**
 * $Id: JuxyTestCase.java,v 1.8 2005-08-11 08:24:37 pavelsher Exp $
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
        return XPathFactory.newXPath(xpathExpr);
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
     * Asserts that two documents are equal. Meaningless spaces will be ignored during this assertion.
     * @param expectedDocument XML document which is expected
     * @param actualDocument actual xml document
     * @throws Exception
     */
    public static void assertXMLEquals(String expectedDocument, String actualDocument) throws Exception {
        XMLComparator.assertEquals(expectedDocument, actualDocument);
    }

    /**
     * For method description see {@link StringUtil#normalizeSpaces(String)}
     * @param str string to normalize
     * @return normalized string
     */
    public String normalizeSpaces(String str) {
        return StringUtil.normalizeSpaces(str);
    }

    /**
     * For method description see {@link StringUtil#normalizeAll(String)}
     * @param str string to normalize
     * @return normalized string
     */
    public String normalizeAll(String str) {
        return StringUtil.normalizeAll(str);
    }

    /**
     * Prints fragment of the document to System.out starting from the specified node.
     * @param node node to display
     */
    public void print(Node node) throws Exception {
        ArgumentAssert.notNull(node, "Node must not be null");
        System.out.println(asString(node));
    }

    /**
     * Serializes fragment of the document to String, starting from the specified node.
     * @param node node to display
     * @return xml document corresponding to the specified node
     */
    public String asString(Node node) throws Exception {
        ArgumentAssert.notNull(node, "Node must not be null");
        ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
        DOMUtil.printDOM(node, bos);
        return bos.toString();
    }

    /**
     * Parses specified string into the DOM Document.
     * @param document xml document
     * @return DOM Document
     */
    public Document parse(String document) throws Exception {
        ArgumentAssert.notEmpty(document, "Document must not be empty");
        return DOMUtil.parse(document);
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
