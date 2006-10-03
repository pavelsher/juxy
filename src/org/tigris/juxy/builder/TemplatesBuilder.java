package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.XPathExpr;

import javax.xml.transform.Templates;
import javax.xml.transform.URIResolver;
import java.util.Collection;
import java.util.Map;

/**
 * @author Pavel Sher
 */
public interface TemplatesBuilder
{
    void setImportSystemId(String systemId, URIResolver resolver);
    void setTracingEnabled(boolean tracingEnabled);
    void setGlobalVariables(Collection variables);
    void setCurrentNode(XPathExpr currentNode);
    void setInvokationStatementInfo(String name, Collection invokeParams);
    void setInvokationStatementInfo(XPathExpr selectXpathExpr, String mode, Collection invokeParams);
    void setNamespaces(Map namespaces);

    Templates build();
}
