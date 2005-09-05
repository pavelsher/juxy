package org.tigris.juxy.verifier;

/**
 * $Id: Factory.java,v 1.1 2005-09-05 17:37:37 pavelsher Exp $
 *
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
