package org.tigris.juxy;

/**
 * $Id: ValueContainer.java,v 1.1 2005-08-11 08:24:37 pavelsher Exp $
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
