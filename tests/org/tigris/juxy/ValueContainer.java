package org.tigris.juxy;

/**
 * $Id: ValueContainer.java,v 1.2 2006-10-31 11:01:22 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class ValueContainer {
  private Object value;

  public void setString(String value) {
    this.value = value;
  }

/*
    public void setNode(Node value) {
        this.value = value;
    }
*/

  public Object getValue() {
    return value;
  }
}
