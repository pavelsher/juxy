package org.tigris.juxy;

import org.tigris.juxy.builder.TemplatesBuilder;
import org.tigris.juxy.builder.TemplatesBuilderImpl;
import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathExpr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

/**
 * This runner uses only standard features. It does not use any xslt engine - specific extensions.
 *
 * @version $Revision: 1.3 $
 * @author Pavel Sher
 */
class RunnerImpl implements Runner
{
    private static TransformerFactory trFactory = null;
    private TemplatesBuilder templatesBuilder = null;
    private static final Log logger = LogFactory.getLog(RunnerImpl.class);

    protected RunnerImpl()
    {
        createTransformerFactory();
        templatesBuilder = new TemplatesBuilderImpl(trFactory);
        checkEnvironment();
    }

    private void checkEnvironment()
    {
        if (!trFactory.getFeature(SAXSource.FEATURE))
            throw new RuntimeException("The specified transformer factory does not support SAXSource");
        if (!trFactory.getFeature(DOMResult.FEATURE))
            throw new RuntimeException("The specified transformer factory does not support DOMResult");
    }

    private void createTransformerFactory()
    {
        trFactory = TransformerFactory.newInstance();
    }

    private Transformer getTransformer() throws TransformerConfigurationException
    {
        Templates templates = templatesBuilder.build();
        if (templates == null)
            throw new RuntimeException("Failed to create transformer");
        return templates.newTransformer();
    }

    public RunnerContext newRunnerContext(String stylesheetPath) throws FileNotFoundException
    {
        File stylesheetFile = new File(stylesheetPath);
        if (!stylesheetFile.isFile())
            throw new FileNotFoundException("Stylesheet file not found: " + stylesheetFile.getAbsolutePath());

        return new RunnerContextImpl(stylesheetFile.toURI().toString());
    }

    public Node callTemplate(RunnerContext ctx, String name) throws TransformerException {
        RunnerContextImpl sctx = getContext(ctx);

        setupTemplatesBuilder(sctx);
        templatesBuilder.setInvokationStatementInfo(name, sctx.getTemplateParams());

        return transformSource(sctx.getSourceDocument(), sctx);
    }

    public Node applyTemplates(RunnerContext ctx) throws TransformerException {
        return applyTemplates(ctx, null, null);
    }

    public Node applyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr) throws TransformerException {
        return applyTemplates(ctx, selectXpathExpr, null);
    }

    public Node applyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr, String mode) throws TransformerException {
        RunnerContextImpl sctx = getContext(ctx);

        setupTemplatesBuilder(sctx);
        templatesBuilder.setInvokationStatementInfo(selectXpathExpr, mode, sctx.getTemplateParams());

        return transformSource(sctx.getSourceDocument(), sctx);
    }

    private RunnerContextImpl getContext(RunnerContext ctx)
    {
        ArgumentAssert.notNull(ctx, "Runner context must not be null");

        RunnerContextImpl st_ctx = (RunnerContextImpl)ctx;
        st_ctx.checkComplete();
        return st_ctx;
    }

    private void setupTemplatesBuilder(RunnerContextImpl ctx)
    {
        templatesBuilder.setImportSystemId(ctx.getStylesheetSystemId());
        templatesBuilder.setCurrentNode(ctx.getCurrentNodeSelector());
        templatesBuilder.setGlobalVariables(ctx.getGlobalVariables());
        templatesBuilder.setNamespaces(ctx.getNamespaces());
    }

    private Node transformSource(Source sourceDoc, RunnerContextImpl ctx) throws TransformerException {
        try
        {
            Transformer transformer = getTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            if (ctx.getGlobalParams() != null)
            {
                Map namespaces = ctx.getNamespaces();
                Iterator it = ctx.getGlobalParams().iterator();
                while(it.hasNext())
                {
                    GlobalParam param = (GlobalParam)it.next();
                    transformer.setParameter(param.getTransformerQName(namespaces), param.getValue());
                }
            }

            // we want to use Xerces DOM instead of XSLT engine internal realization
            Document document = DOMUtil.newDocument();
            DocumentFragment parentNode = document.createDocumentFragment();
            document.appendChild(parentNode);
            DOMResult result = new DOMResult(parentNode);
            transformer.transform(sourceDoc, result);
            parentNode.normalize(); // to put all Text nodes together
            DOMUtil.logDocument("Transformation result:", parentNode);

            return parentNode;
        }
        catch (TransformerConfigurationException ex)
        {
            logger.error("Internal error occured", ex);
            throw ex;
        }
        catch (TransformerException ex)
        {
            logger.error("Error occured during template invokation", ex);
            throw ex;
        }
    }
}
