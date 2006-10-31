package org.tigris.juxy.verifier;

import javax.xml.transform.URIResolver;
import java.util.List;

/**
 * XSLT stylesheets verifier.
 * Verifies that all specified stylesheets are successfully compiled by XSLT transformer.
 */
public interface Verifier {
  /**
   * Sets ErrorReporter to use during verification procedure
   *
   * @param er
   */
  void setErrorReporter(ErrorReporter er);

  /**
   * Sets class name of the TRaX TransformerFactory
   *
   * @param className
   */
  void setTransformerFactory(String className);

  /**
   * Sets URIResolver to use for imports and includes resolution.
   *
   * @param resolver
   */
  void setURIResolver(URIResolver resolver);

  /**
   * Sets files to verify.
   *
   * @param files list of File objects
   */
  void setFiles(List files);

  /**
   * Starts process of stylesheets verification.
   *
   * @param failFast whether to stop verification after the first error, or not.
   * @return true if verification was successful and false otherwise
   */
  boolean verify(boolean failFast);

  /**
   * Returns number of successfully verified files
   *
   * @return number of successfully verified files
   */
  int getNumberOfVerifiedFiles();

  /**
   * Returns number of files that were not verified due to verification errors
   *
   * @return number of files that were not verified due to verification errors
   */
  int getNumberOfNotVerifierFiles();
}
