package org.tigris.juxy.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.juxy.util.SAXUtil;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

/**
 * $Id: TracingURIResolver.java,v 1.1 2005-08-17 17:54:52 pavelsher Exp $
 * <p/>
 * This URI resolver will return SAXSource with tracing filter set up into it.
 * @author Pavel Sher
 */
public class TracingURIResolver implements URIResolver {
    private static final Log logger = LogFactory.getLog(TracingURIResolver.class);
    private URIResolver originalResolver;

    public TracingURIResolver(URIResolver originalResolver) {
        assert originalResolver != null;
        this.originalResolver = originalResolver;
    }

    public Source resolve(String href, String base) throws TransformerException {
        Source source = originalResolver.resolve(href, base);
        if (source == null) return null;
        
        if (source instanceof DOMSource) {
            logger.warn("Tracing is not available for stylesheets passed as DOMSource object");
            return source;
        }

        XMLReader parentReader = null;
        if (source instanceof SAXSource)
            parentReader = ((SAXSource)source).getXMLReader();
        if (parentReader == null)
            parentReader = SAXUtil.newXMLReader();

        XMLFilter tracingFilter = new TracingFilter();
        tracingFilter.setParent(parentReader);

/*
        SimpleSerializer s = new SimpleSerializer();
        s.setOutputStream(System.out);
        s.setParent(tracingFilter);
*/

        SAXSource result = new SAXSource(SAXSource.sourceToInputSource(source));
        result.setXMLReader(tracingFilter);

        return result;
    }
}
