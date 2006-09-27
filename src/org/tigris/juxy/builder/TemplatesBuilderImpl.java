package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.*;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.util.SAXUtil;
import org.tigris.juxy.util.FileURIResolver;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * $Id: TemplatesBuilderImpl.java,v 1.16 2006-09-27 17:13:50 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class TemplatesBuilderImpl implements TemplatesBuilder
{
    private final static String DEFAULT_VERSION = "1.0";

    private String importSystemId;
    private String resolvedSystemId;
    private URIResolver resolver;
    private Source stylesheetSource;

    private Collection globalVariables = null;
    private Map namespaces = new HashMap();
    private XPathExpr currentNode;
    private InvokationStatementInfo invokationStatementInfo = null;
    private boolean newTemplatesRequired = true;
    private Templates currentTemplates = null;
    private Document currentStylesheetDoc = null;
    private boolean tracingEnabled = false;

    private TransformerFactory transformerFactory = null;
    private static final Log logger = LogFactory.getLog(TemplatesBuilderImpl.class);

    public TemplatesBuilderImpl(TransformerFactory trFactory)
    {
        assert trFactory != null;

        this.transformerFactory = trFactory;
        this.transformerFactory.setErrorListener(new BuilderErrorListener());

        this.rootNode = XPathFactory.newXPath("/");
    }

    public void setImportSystemId(String systemId, URIResolver resolver)
    {
        assert systemId != null && systemId.length() > 0;
        if (resolver == null)
            resolver = new FileURIResolver();

        Source src = null;
        try {
            src = resolver.resolve(systemId, "");
            if (src == null)
                throw new JuxyRuntimeException("Failed to resolve system id: " + systemId);
        } catch (TransformerException e) {
            throw new JuxyRuntimeException("Failed to resolve system id: " + systemId, e);
        }

        updateNewTemplateFlag(!src.getSystemId().equals(resolvedSystemId));

        this.stylesheetSource = src;
        this.resolvedSystemId = src.getSystemId();
        this.importSystemId = systemId;
        this.resolver = resolver;
    }

    public void setTracingEnabled(boolean tracingEnabled) {
        updateNewTemplateFlag(this.tracingEnabled != tracingEnabled);
        this.tracingEnabled = tracingEnabled;
    }

    public void setGlobalVariables(Collection variables)
    {
        updateNewTemplateFlag(!isEquivalentCollections(globalVariables, variables));
        this.globalVariables = variables;
    }

    public void setCurrentNode(XPathExpr currentNode)
    {
        if (currentNode != null)
            updateNewTemplateFlag(!currentNode.equals(this.currentNode));
        else if (this.currentNode != null)
            updateNewTemplateFlag(!this.currentNode.equals(currentNode));

        this.currentNode = currentNode;
    }

    public void setInvokationStatementInfo(String name, Collection invokeParams)
    {
        assert name != null;

        InvokationStatementInfo newInvokationStatementInfo = new InvokationStatementInfo(name, invokeParams);
        updateNewTemplateFlag(
                invokationStatementInfo == null ||
                !invokationStatementInfo.isEquivalent(newInvokationStatementInfo)
        );

        invokationStatementInfo = newInvokationStatementInfo;
    }

    public void setInvokationStatementInfo(XPathExpr selectXpathExpr, String mode, Collection invokeParams)
    {
        InvokationStatementInfo newInvokationStatementInfo = new InvokationStatementInfo(selectXpathExpr, mode, invokeParams);
        updateNewTemplateFlag(
                invokationStatementInfo == null ||
                !invokationStatementInfo.isEquivalent(newInvokationStatementInfo)
        );

        invokationStatementInfo = newInvokationStatementInfo;
    }

    public void setNamespaces(Map namespaces)
    {
        if (namespaces == null)
        {
            updateNewTemplateFlag(this.namespaces.size() > 0);
            this.namespaces.clear();
            return;
        }

        updateNewTemplateFlag(namespaces.size() != this.namespaces.size());
        if (!newTemplatesRequired)
            updateNewTemplateFlag(!this.namespaces.equals(namespaces));

        this.namespaces.clear();
        this.namespaces.putAll(namespaces);
    }

    public Templates build()
    {
        if (newTemplatesRequired)
        {
            checkBuildConfiguration();
            String version = getImportedStylesheetVersion();
            Element rootEl = createSkeleton(version);
            createGlobalVariables(rootEl);
            createInvokationStatement(rootEl);
            updateCurrentTemplates(rootEl.getOwnerDocument());
            currentStylesheetDoc = rootEl.getOwnerDocument();
        }

        newTemplatesRequired = false;

        return currentTemplates;
    }

    private String getImportedStylesheetVersion() {
        assert resolvedSystemId != null;
        assert stylesheetSource != null;

        String version = null;
        if (!(stylesheetSource instanceof DOMSource)) {
            XSLVersionRetriever handler = new XSLVersionRetriever();
            try {
                XMLReader reader = SAXUtil.newXMLReader();
                InputSource is = SAXSource.sourceToInputSource(stylesheetSource);
                reader.setContentHandler(handler);
                reader.parse(is);
            } catch (SAXException e) {
                if (!XSLVersionRetriever.STOP_MESSAGE.equals(e.getMessage()))
                    throw new JuxyRuntimeException("XML parse error", e);
            } catch (IOException e) {
                throw new JuxyRuntimeException("Input / output error on attempt to read from stylesheet: " + importSystemId, e);
            }

            version = handler.getVersion();
        } else {
            // obtain xsl version from DOM
            DOMSource ds = (DOMSource)stylesheetSource;
            Node node = ds.getNode();
            if (node == null)
                throw new JuxyRuntimeException("DOMSource Node is null for system id: " + importSystemId);
            Document doc = node.getOwnerDocument();
            if (doc == null && node.getNodeType() == Node.DOCUMENT_NODE)
                doc = (Document) node;
            if (doc == null)
                throw new JuxyRuntimeException("Failed to obtain Document from the DOMSource Node for system id: " + importSystemId);
            NodeList nodes = (NodeList) doc.getElementsByTagNameNS(XSLTKeys.XSLT_NS, "stylesheet");
            if (nodes.getLength() == 0)
                logger.warn("Element xsl:stylesheet was not found in the system id: " + importSystemId);
            else {
                Element stylesheetEl = (Element) nodes.item(0);
                version = stylesheetEl.getAttribute("version");
            }
        }

        if (version == null) {
            logger.warn("Unable to obtain stylesheet version for system id: " + importSystemId + ", version " + DEFAULT_VERSION + " will be used");
            return DEFAULT_VERSION;
        }

        logger.debug("Imported stylesheet version: " + version);
        return version;
    }

    Document getCurrentStylesheetDoc()
    {
        return currentStylesheetDoc;
    }

    private void checkBuildConfiguration()
    {
        if (importSystemId == null || resolvedSystemId == null)
            throw new IllegalStateException("System id not specified");
    }

    private void updateCurrentTemplates(Document stylesheet) {
        assert resolver != null;

        try
        {
            boolean buggyXSLT = transformerFactory.getClass().getName().
                    equals("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl") &&
                    System.getProperty("java.vm.version").startsWith("1.5.");

            if (!buggyXSLT) {
                transformerFactory.setURIResolver(tracingEnabled ? new TracingURIResolver(resolver) : resolver);
            } else {
                if (tracingEnabled) {
                    logger.warn("Tracing is not supported for XSLT transformer bundled with Java 1.5.");
                }
                if (!(resolver instanceof FileURIResolver)) {
                    logger.warn("Custom URI resolver is not supported for XSLT transformer bundled with Java 1.5.");
                }
            }

            DOMSource source = new DOMSource(stylesheet);
            // Setting system id to be in the current directory (we are using some file for that,
            // but it does not matter whether this file exists or not).
            // This system id is required to be able to resolve paths to
            // imported and included stylesheets
            source.setSystemId(new File("generated-stylesheet.xsl").getAbsoluteFile().toURI().toString());
            currentTemplates = transformerFactory.newTemplates(source);
        }
        catch(TransformerConfigurationException ex)
        {
            throw new JuxyRuntimeException("Failed to create Templates object", ex);
        }
    }

    private void createInvokationStatement(Element stylesheetEl)
    {
        if (invokationStatementInfo == null)
            return;

        if (invokationStatementInfo.isNamedTemplateCall())
            createNamedTemplateCall(stylesheetEl);
        else
            createAppliedTemplateCall(stylesheetEl);
    }

    private void createNamedTemplateCall(Element stylesheetEl)
    {
        Element callTemplateEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:call-template");
        callTemplateEl.setAttribute("name", invokationStatementInfo.getTemplateName());
        createCallingStatementParent(stylesheetEl).appendChild(callTemplateEl);
        createInvokationParams(callTemplateEl, invokationStatementInfo.getTemplateInvokeParams());
    }

    private void createAppliedTemplateCall(Element stylesheetEl)
    {
        if ( !rootNode.equals(invokationStatementInfo.getTemplateSelectXPath()) ||
                invokationStatementInfo.getTemplateMode() != null )
        {
            if (invokationStatementInfo.getTemplateSelectXPath() == null &&
                    invokationStatementInfo.getTemplateMode() == null &&
                    (currentNode == null || currentNode.equals(rootNode))) {
                Element applyImportsEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:apply-imports");
                createCallingStatementParent(stylesheetEl).appendChild(applyImportsEl);
                createInvokationParams(applyImportsEl, invokationStatementInfo.getTemplateInvokeParams());
                return;
            }

            Element applyTemplatesEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:apply-templates");
            if (invokationStatementInfo.getTemplateSelectXPath() != null)
                applyTemplatesEl.setAttribute("select", invokationStatementInfo.getTemplateSelectXPath().getExpression());
            if (invokationStatementInfo.getTemplateMode() != null)
                applyTemplatesEl.setAttribute("mode", invokationStatementInfo.getTemplateMode());
            createCallingStatementParent(stylesheetEl).appendChild(applyTemplatesEl);
            createInvokationParams(applyTemplatesEl, invokationStatementInfo.getTemplateInvokeParams());
        }
    }

    private Element createCallingStatementParent(Element stylesheetEl)
    {
        Element rootTemplateEl = createRootTemplate(stylesheetEl);

        if (currentNode == null || rootNode.equals(currentNode))
            return rootTemplateEl;

        Element targetEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:for-each");
        targetEl.setAttribute("select", currentNode.getExpression());
        rootTemplateEl.appendChild(targetEl);

        return targetEl;
    }

    private Element createRootTemplate(Element stylesheetEl)
    {
        Element templateEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:template");
        stylesheetEl.appendChild(templateEl);
        templateEl.setAttribute("match", "/");

        return templateEl;
    }

    private void createInvokationParams(Element invokeEl, Collection params)
    {
        if (params == null || params.isEmpty())
            return;

        Iterator it = params.iterator();
        while (it.hasNext())
        {
            InvokeParam param = (InvokeParam)it.next();
            Element paramEl = invokeEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:with-param");
            invokeEl.appendChild(paramEl);

            setVariableContentAndAttributes(param, paramEl);
        }
    }

    private void createGlobalVariables(Element stylesheetEl)
    {
        if (globalVariables == null || globalVariables.size() == 0)
            return;

        Iterator varIt = globalVariables.iterator();
        while(varIt.hasNext())
        {
            GlobalVariable var = (GlobalVariable)varIt.next();
            Element variableEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:variable");
            stylesheetEl.appendChild(variableEl);

            setVariableContentAndAttributes(var, variableEl);
        }
    }

    private void setVariableContentAndAttributes(VariableBase var, Element variableEl)
    {
        Document doc = variableEl.getOwnerDocument();
        variableEl.setAttribute("name", var.getQname());
        if (var.isVariableWithContent())
            variableEl.appendChild( doc.importNode(var.getContent().getDocumentElement(), true) );
        else if (var.isXPathValue())
            variableEl.setAttribute("select", var.getXPathValue());
        else if (var.getStringValue() != null)
            variableEl.appendChild(doc.createTextNode(var.getStringValue()));
        else
            variableEl.setAttribute("select", "/.."); // set empty node set as param value
    }

    private Element createSkeleton(String version) {
        assert version != null;

        Document stylesheetDoc;
        stylesheetDoc = DOMUtil.newDocument();

        Element stylesheetEl = stylesheetDoc.createElementNS(XSLTKeys.XSLT_NS, "xsl:stylesheet");
        stylesheetEl.setAttribute("version", version);
        stylesheetDoc.appendChild(stylesheetEl);

        registerNamespaces(stylesheetEl);

        createImport(stylesheetEl);

        if (tracingEnabled) {
            Element traceParamEl = stylesheetDoc.createElementNS(XSLTKeys.XSLT_NS, "xsl:param");
            traceParamEl.setAttributeNS(XMLNS_NS, "xmlns:" + JuxyParams.PREFIX, JuxyParams.NS);
            traceParamEl.setAttribute("name", JuxyParams.PREFIX + ":" + JuxyParams.TRACE_PARAM);
            stylesheetEl.appendChild(traceParamEl);
        }

        return stylesheetEl;
    }

    private void registerNamespaces(Element stylesheetEl)
    {
        Iterator it = namespaces.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry ns = (Map.Entry)it.next();
            String uri = (String)ns.getKey();
            String prefix = (String)ns.getValue();
            String qname = prefix != null && prefix.length() > 0 ? "xmlns:" + prefix : "xmlns";
            stylesheetEl.setAttributeNS(XMLNS_NS, qname, uri);
        }
    }

    private void createImport(Element stylesheetEl)
    {
        Element importEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.XSLT_NS, "xsl:import");
        stylesheetEl.appendChild(importEl);

        importEl.setAttribute("href", importSystemId);
    }

    private void updateNewTemplateFlag(boolean needNew)
    {
        newTemplatesRequired = newTemplatesRequired || needNew;
    }

    class InvokationStatementInfo
    {
        private String templateName = null;
        private Collection templateInvokeParams = null;
        private XPathExpr templateSelectXPath = null;
        private String templateMode = null;
        private boolean namedTemplateCall;

        public InvokationStatementInfo(String templateName, Collection templateInvokeParams)
        {
            this.templateName = templateName;
            this.templateInvokeParams = templateInvokeParams;
            namedTemplateCall = true;
        }

        public InvokationStatementInfo(XPathExpr templateSelectXPath, String templateMode, Collection templateInvokeParams)
        {
            this.templateSelectXPath = templateSelectXPath;
            this.templateMode = templateMode;
            this.templateInvokeParams = templateInvokeParams;
            namedTemplateCall = false;
        }

        public boolean isEquivalent(InvokationStatementInfo newStatement)
        {
            if (newStatement.isNamedTemplateCall() != isNamedTemplateCall())
                return false;

            if (newStatement.isNamedTemplateCall())
            {
                return isEquivalentInvokePrams(newStatement.getTemplateInvokeParams()) &&
                        newStatement.getTemplateName().equals(templateName);
            }

            return isEquivalentInvokePrams(newStatement.getTemplateInvokeParams()) &&
                    isEquivalentSelectXPath(newStatement.getTemplateSelectXPath()) &&
                    isEquivalentMode(newStatement.getTemplateMode());
        }

        public String getTemplateName()
        {
            return templateName;
        }

        public Collection getTemplateInvokeParams()
        {
            return templateInvokeParams;
        }

        public XPathExpr getTemplateSelectXPath()
        {
            return templateSelectXPath;
        }

        public String getTemplateMode()
        {
            return templateMode;
        }

        private boolean isEquivalentInvokePrams(Collection newInvokeParams)
        {
            return isEquivalentCollections(templateInvokeParams, newInvokeParams);
        }

        private boolean isEquivalentSelectXPath(XPathExpr newSelectXpath)
        {
            if (newSelectXpath != null)
                return newSelectXpath.equals(templateSelectXPath);

            return templateSelectXPath == null;
        }

        private boolean isEquivalentMode(String newMode)
        {
            if (newMode != null)
                return newMode.equals(templateMode);

            return templateMode == null;
        }

        public boolean isNamedTemplateCall()
        {
            return namedTemplateCall;
        }
    }

    private boolean isEquivalentCollections(Collection orig, Collection newc)
    {
        if (newc != null && newc.size() > 0)
            return false;

        if (orig == null || orig.size() == 0)
            return true;

        return false; // TODO
    }

    private XPathExpr rootNode = null;
    static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
}
