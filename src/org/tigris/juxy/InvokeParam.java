package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * <p/>
 *
 * @author Pavel Sher
 */
public class InvokeParam extends VariableBase {
  public InvokeParam(String qname, String value) {
    super(qname, value);
  }

  public InvokeParam(String qname, XPathExpr xpath) {
    super(qname, xpath);
  }

  public InvokeParam(String qname, Document content) {
    super(qname, content);
  }
}
