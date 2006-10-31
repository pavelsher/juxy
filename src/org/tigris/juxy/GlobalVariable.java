package org.tigris.juxy;

import org.tigris.juxy.xpath.XPathExpr;
import org.w3c.dom.Document;

/**
 * <p/>
 *
 * @author Pavel Sher
 */
public class GlobalVariable extends VariableBase {
  public GlobalVariable(final String qname, final String value) {
    super(qname, value);
  }

  public GlobalVariable(final String qname, final XPathExpr xpath) {
    super(qname, xpath);
  }

  public GlobalVariable(final String qname, final Document content) {
    super(qname, content);
  }
}
