package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id: RunnerContextImpl.java,v 1.6 2005-08-10 08:57:18 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
class RunnerContextImpl implements RunnerContext
{
    private String systemId = null;
    private URIResolver resolver = null;
    private SourceDocument sourceDocument = null;
    private XPathExpr currentNodeSelector = null;
    private Map globalParams = null;
    private Map globalVariables = null;
    private Map templateParams = null;
    private Map namespaces = null;

    protected RunnerContextImpl(String systemId)
    {
        ArgumentAssert.notEmpty(systemId, "System id must not be empty");

        this.systemId = systemId;

        globalParams = new HashMap();
        globalVariables = new HashMap();
        templateParams = new HashMap();
        namespaces = new HashMap();
    }

    protected RunnerContextImpl(String systemId, URIResolver resolver)
    {
        ArgumentAssert.notEmpty(systemId, "System id must not be empty");
        ArgumentAssert.notNull(resolver, "URIResolver must not be null");

        this.systemId = systemId;
        this.resolver = resolver;

        globalParams = new HashMap();
        globalVariables = new HashMap();
        templateParams = new HashMap();
        namespaces = new HashMap();
    }

    public void setDocument(String documentContent)
    {
        ArgumentAssert.notEmpty(documentContent, "Input document must not be empty");
        sourceDocument = new SourceDocument(documentContent);
    }

    public void setDocument(Document document) {
        ArgumentAssert.notNull(document, "Input document must not be null");
        sourceDocument = new SourceDocument(document);
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

    public void setCurrentNode(XPathExpr xpathExpr)
    {
        ArgumentAssert.notNull(xpathExpr, "XPath expression must not be null");

        currentNodeSelector = xpathExpr;
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

    public void setGlobalVariableValue(String varName, XPathExpr xpath)
    {
        globalVariables.put(varName, new GlobalVariable(varName, xpath));
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

    public void setTemplateParamValue(String paramName, XPathExpr xpath)
    {
        this.templateParams.put(paramName, new InvokeParam(paramName, xpath));
    }

    public void setTemplateParamValue(String paramName, Document paramContent)
    {
        this.templateParams.put(paramName, new InvokeParam(paramName, paramContent));
    }

    public void clearTemplateParams()
    {
        templateParams.clear();
    }

    protected String getSystemId()
    {
        return systemId;
    }

    protected Source getSourceDocument()
    {
        return sourceDocument.toSource();
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

    protected URIResolver getResolver() {
        return resolver;
    }

    void checkComplete()
    {
        if (sourceDocument == null)
            throw new IllegalStateException("Input document was not specified, call setDocument() method first");
    }

    class SourceDocument {
        private String content;
        private Document document;

        public SourceDocument(String content) {
            assert content != null;
            this.content = content;
        }

        public SourceDocument(Document document) {
            assert document != null;
            this.document = document;
        }

        public Source toSource() {
            if (content != null)
                return toSAXSource(content);

            return new DOMSource(document);
        }

        private SAXSource toSAXSource(String documentContent)
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(documentContent.getBytes());
            InputSource is = new InputSource(bais);

            return new SAXSource(is);
        }
    }
}
