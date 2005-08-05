package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id: RunnerContextImpl.java,v 1.3 2005-08-05 08:38:29 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
class RunnerContextImpl implements RunnerContext
{
    private String stylesheetSystemId = null;
    private Source sourceDocument = null;
    private String sourceDocumentContent = null;
    private XPathExpr currentNodeSelector = null;
    private Map globalParams = null;
    private Map globalVariables = null;
    private Map templateParams = null;
    private Map namespaces = null;

    protected RunnerContextImpl(String stylesheetSystemId)
    {
        this.stylesheetSystemId = stylesheetSystemId;
        globalParams = new HashMap();
        globalVariables = new HashMap();
        templateParams = new HashMap();
        namespaces = new HashMap();
    }

    public void setDocument(String documentContent)
    {
        ArgumentAssert.notEmpty(documentContent, "The source document must not be empty");
        sourceDocumentContent = documentContent;
    }

    public void registerNamespace(String prefix, String uri)
    {
        ArgumentAssert.notNull(prefix, "Prefix must not be null");
        ArgumentAssert.notEmpty(uri, "URI must not be null or empty");
        namespaces.put(uri, prefix);
    }

    public void clearNamespaces()
    {
        namespaces.clear();
    }

    public void setCurrentNode(XPathExpr selectXpathExpr)
    {
        currentNodeSelector = selectXpathExpr;
    }

    public void setGlobalParamValue(String name, Object value)
    {
        globalParams.put(name, new GlobalParam(name, value));
    }

    public void clearGlobalParams()
    {
        globalParams.clear();
    }

    public void setGlobalVariableValue(String varName, String varValue)
    {
        globalVariables.put(varName, new GlobalVariable(varName, varValue));
    }

    public void setGlobalVariableValue(String varName, XPathExpr selectXpathExpr)
    {
        globalVariables.put(varName, new GlobalVariable(varName, selectXpathExpr));
    }

    public void setGlobalVariableValue(String varName, Document varContent)
    {
        globalVariables.put(varName, new GlobalVariable(varName, varContent));
    }

    public void clearGlobalVariables()
    {
        globalVariables.clear();
    }

    public void setTemplateParamValue(String paramName, String paramValue)
    {
        this.templateParams.put(paramName, new InvokeParam(paramName, paramValue));
    }

    public void setTemplateParamValue(String paramName, XPathExpr selectXpathExpr)
    {
        this.templateParams.put(paramName, new InvokeParam(paramName, selectXpathExpr));
    }

    public void setTemplateParamValue(String paramName, Document paramContent)
    {
        this.templateParams.put(paramName, new InvokeParam(paramName, paramContent));
    }

    public void clearTemplateParams()
    {
        templateParams.clear();
    }

    protected String getStylesheetSystemId()
    {
        return stylesheetSystemId;
    }

    protected Source getSourceDocument()
    {
        if (sourceDocument == null && sourceDocumentContent != null)
            return toSAXSource(sourceDocumentContent);

        return sourceDocument;
    }

    protected XPathExpr getCurrentNodeSelector()
    {
        return currentNodeSelector;
    }

    protected Map getNamespaces()
    {
        return namespaces;
    }

    protected Collection getGlobalParams()
    {
        return globalParams.values();
    }

    protected Collection getGlobalVariables()
    {
        return globalVariables.values();
    }

    protected Collection getTemplateParams()
    {
        return templateParams.values();
    }

    void checkComplete()
    {
        if (sourceDocument == null && sourceDocumentContent == null)
            throw new IllegalStateException("The source document must not be empty, call setDocument() first");
    }

    public SAXSource toSAXSource(String documentContent)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(documentContent.getBytes());
        InputSource is = new InputSource(bais);

        return new SAXSource(is);
    }
}
