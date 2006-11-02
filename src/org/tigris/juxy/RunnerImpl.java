package org.tigris.juxy;

import org.tigris.juxy.builder.JuxyParams;
import org.tigris.juxy.builder.TemplatesBuilder;
import org.tigris.juxy.builder.TemplatesBuilderImpl;
import org.tigris.juxy.util.ArgumentAssert;
import org.tigris.juxy.util.DOMUtil;
import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.util.Iterator;
import java.util.Map;

/**
 * $Id: RunnerImpl.java,v 1.13 2006-11-02 10:13:07 pavelsher Exp $
 * <p/>
 * This runner uses only standard features. It does not use any XSLT engine-specific extensions.
 *
 * @author Pavel Sher
 */
class RunnerImpl implements Runner {
  private static TransformerFactory trFactory = null;
  private TemplatesBuilder templatesBuilder = null;
  private boolean tracingEnabled = false;

  protected RunnerImpl() {
    createTransformerFactory();
    templatesBuilder = new TemplatesBuilderImpl(trFactory);
    checkEnvironment();
  }

  private void checkEnvironment() {
    if (!trFactory.getFeature(SAXSource.FEATURE))
      throw new JuxyRuntimeException("The specified transformer factory does not support SAXSource");
    if (!trFactory.getFeature(DOMResult.FEATURE))
      throw new JuxyRuntimeException("The specified transformer factory does not support DOMResult");
  }

  private void createTransformerFactory() {
    trFactory = TransformerFactory.newInstance();
  }

  private Transformer getTransformer() throws TransformerConfigurationException {
    Templates templates = templatesBuilder.build();
    if (templates == null)
      throw new JuxyRuntimeException("Failed to create transformer, check Juxy logs for details");
    return templates.newTransformer();
  }

  public RunnerContext newRunnerContext(String systemId) {
    ArgumentAssert.notEmpty(systemId, "System id must not be empty");

    return new RunnerContextImpl(systemId);
  }

  public RunnerContext newRunnerContext(String systemId, URIResolver resolver) {
    ArgumentAssert.notEmpty(systemId, "System id must not be empty");
    ArgumentAssert.notNull(resolver, "Resolver must not be null");

    return new RunnerContextImpl(systemId, resolver);
  }

  public Node callTemplate(RunnerContext ctx, String name) throws TransformerException {
    ArgumentAssert.notNull(ctx, "RunnerContext must not be null");
    ArgumentAssert.notEmpty(name, "Template name must not be empty");

    RunnerContextImpl sctx = getContext(ctx);

    setupTemplatesBuilder(sctx);
    templatesBuilder.setInvokationStatementInfo(name, sctx.getTemplateParams());

    return transformSource(sctx.getSourceDocument(), sctx);
  }

  public Node applyTemplates(RunnerContext ctx) throws TransformerException {
    ArgumentAssert.notNull(ctx, "RunnerContext must not be null");

    return internalApplyTemplates(ctx, null, null);
  }

  public Node applyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr) throws TransformerException {
    ArgumentAssert.notNull(ctx, "RunnerContext must not be null");
    ArgumentAssert.notNull(selectXpathExpr, "XPath expression must not be null");

    return internalApplyTemplates(ctx, selectXpathExpr, null);
  }

  public Node applyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr, String mode) throws TransformerException {
    ArgumentAssert.notNull(ctx, "RunnerContext must not be null");
    ArgumentAssert.notNull(selectXpathExpr, "XPath expression must not be null");
    ArgumentAssert.notEmpty(mode, "Mode must not be empty");

    return internalApplyTemplates(ctx, selectXpathExpr, mode);
  }

  public void enableTracing() {
    tracingEnabled = true;
  }

  public void disableTracing() {
    tracingEnabled = false;
  }

  private Node internalApplyTemplates(RunnerContext ctx, XPathExpr selectXpathExpr, String mode) throws TransformerException {
    RunnerContextImpl sctx = getContext(ctx);

    setupTemplatesBuilder(sctx);
    templatesBuilder.setInvokationStatementInfo(selectXpathExpr, mode, sctx.getTemplateParams());

    return transformSource(sctx.getSourceDocument(), sctx);
  }

  private RunnerContextImpl getContext(RunnerContext ctx) {
    ArgumentAssert.notNull(ctx, "Runner context must not be null");

    RunnerContextImpl st_ctx = (RunnerContextImpl) ctx;
    st_ctx.checkComplete();
    return st_ctx;
  }

  private void setupTemplatesBuilder(RunnerContextImpl ctx) {
    templatesBuilder.setImportSystemId(ctx.getSystemId(), ctx.getResolver());
    templatesBuilder.setTracingEnabled(isTracingEnabled());
    templatesBuilder.setCurrentNode(ctx.getCurrentNodeSelector());
    templatesBuilder.setGlobalVariables(ctx.getGlobalVariables());
    templatesBuilder.setNamespaces(ctx.getNamespaces());
  }

  public boolean isTracingEnabled() {
    String propertyValue = System.getProperty(JuxyProperties.XSLT_TRACING_PROPERTY);
    if ("on".equals(propertyValue)) return true;
    if ("off".equals(propertyValue)) return false;
    return tracingEnabled;
  }

  private Node transformSource(Source sourceDoc, RunnerContextImpl ctx) throws TransformerException {
    Transformer transformer = getTransformer();
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    Tracer traceLogger = null;

    if (ctx.getGlobalParams() != null) {
      Map namespaces = ctx.getNamespaces();
      Iterator it = ctx.getGlobalParams().iterator();
      while (it.hasNext()) {
        GlobalParam param = (GlobalParam) it.next();
        transformer.setParameter(param.getTransformerQName(namespaces), param.getValue());
      }

      if (isTracingEnabled()) {
        traceLogger = new Tracer(System.out);
        transformer.setParameter("{" + JuxyParams.NS + "}" + JuxyParams.TRACE_PARAM, traceLogger);
      }
    }

    Document document = DOMUtil.newDocument();
    DocumentFragment fragment = document.createDocumentFragment();
    DOMResult result = new DOMResult(fragment);
    transformer.transform(sourceDoc, result);
    fragment.normalize();

    if (traceLogger != null)
      traceLogger.stopTracing();

    DOMUtil.logDocument("Transformation result:", fragment);

    return (Document) ResultDocumentProxy.newInstance(document, fragment);
  }
}
