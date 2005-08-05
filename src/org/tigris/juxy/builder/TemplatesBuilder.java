package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.XPathExpr;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import java.util.Collection;
import java.util.Map;

/**
 * $Id: TemplatesBuilder.java,v 1.2 2005-08-05 08:31:10 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public interface TemplatesBuilder
{
    void setImportSystemId(String systemId);
    void setGlobalVariables(Collection variables);
    void setCurrentNode(XPathExpr currentNode);
    void setInvokationStatementInfo(String name, Collection invokeParams);
    void setInvokationStatementInfo(XPathExpr selectXpathExpr, String mode, Collection invokeParams);
    void setNamespaces(Map namespaces);

    Templates build() throws TransformerConfigurationException;
}
