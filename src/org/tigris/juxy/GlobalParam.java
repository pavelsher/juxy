package org.tigris.juxy;

import org.tigris.juxy.util.ArgumentAssert;

import java.util.Map;
import java.util.Iterator;

/**
 * @version $Revision: 1.1 $
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
