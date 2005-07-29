package org.tigris.juxy.builder;

import org.tigris.juxy.xpath.XPathExpr;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import java.util.Collection;
import java.util.Map;

/**
 * Generic interface used for Templates object building.
 *
 * @version $Revision: 1.1 $
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
