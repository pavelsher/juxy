package org.tigris.juxy.util;

import javax.xml.transform.TransformerFactory;

/**
 * @author Pavel Sher
 */
public class XSLTEngineSupport {
  private TransformerFactory factory;

  public XSLTEngineSupport() {
    this.factory = TransformerFactory.newInstance();
  }

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

  public boolean isSaxon8() {
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
    return !isJavaXalanXSLTC() && !isOracleXDK();
  }

  public boolean isXalanXSLTC() {
    return "org.apache.xalan.xsltc.trax.TransformerFactoryImpl".equals(
        factory.getClass().getName());
  }

  public boolean isXalanXSLT() {
    return "org.apache.xalan.processor.TransformerFactoryImpl".equals(
        factory.getClass().getName());
  }

  public String getJavaExtensionNamespace(Class clazz) {
    if (isXalanXSLTC()) return "http://xml.apache.org/xalan/xsltc/java/" + clazz.getName();
    if (isXalanXSLT()) return "http://xml.apache.org/xslt/java/" + clazz.getName();
    if (isOracleXDK()) return "http://www.oracle.com/XSL/Transform/java/" + clazz.getName();
    return "java:" + clazz.getName();
  }
}
