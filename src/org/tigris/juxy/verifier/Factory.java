package org.tigris.juxy.verifier;

/**
 * @author Pavel Sher
 */
public class Factory {
  private String factoryClassName = null;

  public void setName(String name) {
    this.factoryClassName = name;
  }

  public String getFactoryClassName() {
    return factoryClassName;
  }
}
