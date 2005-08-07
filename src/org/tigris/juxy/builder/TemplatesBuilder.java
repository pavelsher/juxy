package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.XPathExpr;

import javax.xml.transform.Templates;
import javax.xml.transform.URIResolver;
import java.util.Collection;
import java.util.Map;

/**
 * $Id: TemplatesBuilder.java,v 1.4 2005-08-07 16:43:15 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public interface TemplatesBuilder
{
    void setImportSystemId(String systemId, URIResolver resolver);
    void setGlobalVariables(Collection variables);
    void setCurrentNode(XPathExpr currentNode);
    void setInvokationStatementInfo(String name, Collection invokeParams);
    void setInvokationStatementInfo(XPathExpr selectXpathExpr, String mode, Collection invokeParams);
    void setNamespaces(Map namespaces);

    Templates build();
}
