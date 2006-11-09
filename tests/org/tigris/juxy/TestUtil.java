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
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isOracleXDK();
  }

  public static boolean isJavaXalanXSLTC() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isJavaXalanXSLTC();
  }

  public static boolean isXalanXSLTC() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isXalanXSLTC();
  }

  public static boolean isURIResolverUsedByDocumentFunction() {
    XSLTEngineSupport engineSupport = new XSLTEngineSupport(TransformerFactory.newInstance());
    return !engineSupport.isXalanXSLTC() && !engineSupport.isXalanXSLT() && !engineSupport.isJavaXalanXSLTC();
  }

  public static boolean isXSLT20Supported() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isXSLT20Supported();
  }

  public static boolean isTracingSupported() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isTracingSupported();
  }

  public static boolean isCustomURIResolverSupported() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isCustomURIResolverSupported();
  }

  /**
   * Oracle XSLT incorrectly sets base URI for imported and included stylesheets.
   * @return
   */
  public static boolean isIncorrectBaseURIForImportedStylesheets() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isOracleXDK();
  }

  public static boolean isExternalJavaFunctionsSupported() {
    return new XSLTEngineSupport(TransformerFactory.newInstance()).isExternalJavaFunctionsSupported();
  }
}
