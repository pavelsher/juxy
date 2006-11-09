package org.tigris.juxy;

import org.xml.sax.InputSource;
import org.tigris.juxy.util.XSLTEngineSupport;

import javax.xml.transform.TransformerFactory;
import java.io.ByteArrayInputStream;

/**
 */
public class TestUtil {
  public static InputSource makeInputSource(String systemId, String data) {
    InputSource src = new InputSource();
    src.setSystemId(systemId);
    src.setByteStream(new ByteArrayInputStream(data.getBytes()));
    return src;
  }

  public static boolean isOracleXDK() {
    return new XSLTEngineSupport().isOracleXDK();
  }

  public static boolean isJavaXalanXSLTC() {
    return new XSLTEngineSupport().isJavaXalanXSLTC();
  }

  public static boolean isXalanXSLTC() {
    return new XSLTEngineSupport().isXalanXSLTC();
  }

  public static boolean isURIResolverUsedByDocumentFunction() {
    XSLTEngineSupport engineSupport = new XSLTEngineSupport();
    return !engineSupport.isXalanXSLTC() && !engineSupport.isXalanXSLT() && !engineSupport.isJavaXalanXSLTC();
  }

  public static boolean isXSLT20Supported() {
    return new XSLTEngineSupport().isXSLT20Supported();
  }

  public static boolean isTracingSupported() {
    return new XSLTEngineSupport().isTracingSupported();
  }

  public static boolean isCustomURIResolverSupported() {
    return new XSLTEngineSupport().isCustomURIResolverSupported();
  }

  /**
   * Oracle XSLT incorrectly sets base URI for imported and included stylesheets.
   * @return
   */
  public static boolean isIncorrectBaseURIForImportedStylesheets() {
    return new XSLTEngineSupport().isOracleXDK();
  }

  public static boolean isExternalJavaFunctionsSupported() {
    final XSLTEngineSupport xsltEngineSupport = new XSLTEngineSupport();
    return xsltEngineSupport.isXalanXSLT() || xsltEngineSupport.isSaxon8();
  }
}
