package org.tigris.juxy.builder;

import org.tigris.juxy.GlobalVariable;
import org.tigris.juxy.InvokeParam;
import org.tigris.juxy.VariableBase;
import org.tigris.juxy.XSLTKeys;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathExpr;
import org.tigris.juxy.xpath.XPathExpressionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @version $Revision: 1.1 $
 * @author Pavel Sher
 */
public class TemplatesBuilderImpl implements TemplatesBuilder
{
    private String importSystemId = null;
    private Collection globalVariables = null;
    private Map namespaces = new HashMap();
    private XPathExpr currentNode;
    private InvokationStatementInfo invokationStatementInfo = null;
    private boolean newTemplatesRequired = true;
    private Templates currentTemplates = null;
    private Document currentStylesheetDoc = null;

    private TransformerFactory transformerFactory = null;
    private static final Log logger = LogFactory.getLog(TemplatesBuilderImpl.class);

    public TemplatesBuilderImpl(TransformerFactory trFactory)
    {
        assert trFactory != null;

        this.transformerFactory = trFactory;
        this.transformerFactory.setErrorListener(new BuilderErrorListener());

        try
        {
            rootNode = new XPathExpr("/");
        }
        catch (XPathExpressionException e)
        {
            logger.error("Internal error occured", e);
        }
    }

    public void setImportSystemId(String systemId)
    {
        checkNotEmpty(systemId, "system id");
        updateNewTemplateFlag(!systemId.equals(importSystemId));
        this.importSystemId = systemId;
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
        checkNotEmpty(name, "name");

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

    public Templates build() throws TransformerConfigurationException
    {
        if (newTemplatesRequired)
        {
            checkBuildConfiguration();
            Element rootEl = createSkeleton();
            createGlobalVariables(rootEl);
            createInvokationStatement(rootEl);
            updateCurrentTemplates(rootEl.getOwnerDocument());
            currentStylesheetDoc = rootEl.getOwnerDocument();
        }

        newTemplatesRequired = false;

        return currentTemplates;
    }

    Document getCurrentStylesheetDoc()
    {
        return currentStylesheetDoc;
    }

    private void checkBuildConfiguration()
    {
        if (importSystemId == null)
            throw new IllegalStateException("Call setImportSystemId() first");
    }

    private void updateCurrentTemplates(Document stylesheet) throws TransformerConfigurationException {
        try
        {
            currentTemplates = transformerFactory.newTemplates(new DOMSource(stylesheet.getDocumentElement()));
        }
        catch(TransformerConfigurationException ex)
        {
            logger.error("Internal error occured", ex);
            throw ex;
        }

        DOMUtil.logDocument("Updated stylesheet", stylesheet);
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
        Element callTemplateEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:call-template");
        callTemplateEl.setAttribute("name", invokationStatementInfo.getTemplateName());
        createCallingStatementParent(stylesheetEl).appendChild(callTemplateEl);
        createInvokationParams(callTemplateEl, invokationStatementInfo.getTemplateInvokeParams());
    }

    private void createAppliedTemplateCall(Element stylesheetEl)
    {
        if ( !rootNode.equals(invokationStatementInfo.getTemplateSelectXPath()) ||
                invokationStatementInfo.getTemplateMode() != null )
        {
            Element applyTemplatesEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:apply-templates");
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

        Element targetEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:for-each");
        targetEl.setAttribute("select", currentNode.getExpression());
        rootTemplateEl.appendChild(targetEl);

        return targetEl;
    }

    private Element createRootTemplate(Element stylesheetEl)
    {
        Element templateEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:template");
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
            Element paramEl = invokeEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:with-param");
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
            Element variableEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:variable");
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

    private Element createSkeleton()
    {
        Document stylesheetDoc;
        stylesheetDoc = DOMUtil.newDocument();

        Element stylesheetEl = stylesheetDoc.createElementNS(XSLTKeys.xslt10Namespace, "xsl:stylesheet");
        stylesheetEl.setAttribute("version", "1.0");
        stylesheetDoc.appendChild(stylesheetEl);

        registerNamespaces(stylesheetEl);

        createImport(stylesheetEl);

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
            stylesheetEl.setAttributeNS(xmlnsNS, "xmlns:" + prefix, uri);
        }
    }

    private void createImport(Element stylesheetEl)
    {
        Element importEl = stylesheetEl.getOwnerDocument().createElementNS(XSLTKeys.xslt10Namespace, "xsl:import");
        stylesheetEl.appendChild(importEl);

        importEl.setAttribute("href", importSystemId);
    }

    private void checkNotEmpty(String value, String valueName)
    {
        if (value == null || value.trim().length() == 0)
            throw new IllegalArgumentException("The " + valueName + " must not be empty");
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
    static final String xmlnsNS = "http://www.w3.org/2000/xmlns/";
}
