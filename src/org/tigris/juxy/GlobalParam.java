package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;

import java.util.Map;
import java.util.Iterator;

/**
 * $Id: GlobalParam.java,v 1.4 2005-08-10 08:57:18 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class GlobalParam
{
    private final String qname;
    private final Object value;
    private final boolean hasPrefix;
    private final String prefix;
    private final String localName;

    public GlobalParam(String qname, Object value)
    {
        ArgumentAssert.notEmpty(qname, "The qname must not be empty");
        ArgumentAssert.notNull(value, "The value must not be null");

        this.qname = qname;
        this.value = value;
        int colonIdx = this.qname.indexOf(":");
        hasPrefix = colonIdx > 0;

        prefix = hasPrefix ? this.qname.substring(0, colonIdx) : "";
        localName = this.qname.substring(colonIdx + 1);
    }

    public String getLocalName()
    {
        return localName;
    }

    public String getNamePrefix()
    {
        return prefix;
    }

    public Object getValue()
    {
        return value;
    }

    public boolean hasPrefix()
    {
        return hasPrefix;
    }

    public String getTransformerQName(Map namespaces)
    {
        if (hasPrefix) {
            String nsURI = findNamespaceURI(namespaces);
            if (nsURI != null)
                return "{" + nsURI + "}" + localName;
        }

        return localName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GlobalParam)) return false;

        final GlobalParam globalParam = (GlobalParam) o;

        if (!qname.equals(globalParam.qname)) return false;
        if (!value.equals(globalParam.value)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = qname.hashCode();
        result = 29 * result + value.hashCode();
        return result;
    }

    private String findNamespaceURI(Map namespaces) {
        String nsURI = null;
        Iterator it = namespaces.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            String p = (String) e.getValue();
            if (prefix.equals(p)) {
                nsURI = (String) e.getKey();
            }
        }
        return nsURI;
    }
}
