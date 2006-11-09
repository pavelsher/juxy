package org.tigris.juxy.util;

import javax.xml.transform.TransformerFactory;

/**
 * @author Pavel Sher
 */
public class XSLTEngineSupport {
  private TransformerFactory factory;

  public XSLTEngineSupport(final TransformerFactory factory) {
    this.factory = factory;
  }

  public boolean isOracleXDK() {
    return "oracle.xml.jaxp.JXSAXTransformerFactory".equals(
        factory.getClass().getName());
  }

  public boolean isXSLT20Supported() {
    return isSaxon8();
  }

  private boolean isSaxon8() {
    return "net.sf.saxon.TransformerFactoryImpl".equals(
        factory.getClass().getName());
  }

  public boolean isCustomURIResolverSupported() {
    return !(isJavaXalanXSLTC() && System.getProperty("java.vm.version").startsWith("1.5."));
  }

  public boolean isJavaXalanXSLTC() {
    return "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl".equals(
        factory.getClass().getName());
  }

  public boolean isTracingSupported() {
    return !isJavaXalanXSLTC() && !isXalanXSLTC() && !isOracleXDK();
  }

  public boolean isXalanXSLTC() {
    return "org.apache.xalan.xsltc.trax.TransformerFactoryImpl".equals(
        factory.getClass().getName());
  }

  public boolean isXalanXSLT() {
    return "org.apache.xalan.processor.TransformerFactoryImpl".equals(
        factory.getClass().getName());
  }

  public boolean isExternalJavaFunctionsSupported() {
    return !isJavaXalanXSLTC() && !isXalanXSLTC() && !isOracleXDK();
  }
}
