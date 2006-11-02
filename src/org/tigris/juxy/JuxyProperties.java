package org.tigris.juxy;

/**
 * @author Pavel Sher
 */
public interface JuxyProperties {
  /**
   * This system property controls XSLT tracing. Possible values are "on" and "off".
   * <p/>
   * This property has higher priority then corresponding methods of Runner
   * {@link Runner#enableTracing()}, {@link Runner#disableTracing()}.
   */
  String XSLT_TRACING_PROPERTY = "xslt.tracing";
}
