package org.tigris.juxy.verifier;

import org.tigris.juxy.builder.FileURIResolver;
import org.tigris.juxy.util.SAXUtil;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 */
public class VerifierImpl implements Verifier {
    private Set urisToVerify = new HashSet();
    private ErrorReporter er;
    private URIResolver resolver = new FileURIResolver();
    private int numberOfErrors = 0;

    public void setErrorReporter(ErrorReporter er) {
        this.er = er;
    }

    public void setURIResolver(URIResolver resolver) {
        assert resolver != null;
        this.resolver = resolver;
    }

    public void setFiles(List files) {
        assert files != null;
        Iterator it = files.iterator();
        while (it.hasNext()) {
            File f = (File) it.next();
            if (f.exists())
                urisToVerify.add(f.toURI().normalize());
        }
    }

    public boolean verify(boolean failFast) {
        try {
            Map links = new HashMap();
            IncludeInstructionsHandler iih = new IncludeInstructionsHandler();
            Iterator it = urisToVerify.iterator();
            while (it.hasNext()) {
                URI fileURI = (URI) it.next();
                Source src = new StreamSource(fileURI.toString());
                registerStylesheet(fileURI, src, links);
                XMLReader reader = SAXUtil.newXMLReader();
                reader.setContentHandler(iih);
                iih.reset();
                try {
                    reader.parse(fileURI.toString());
                } catch (IOException e) {
                    reportError("Failed to parse " + fileURI + " due to the error: " + e.getMessage(), failFast);
                } catch (SAXException e) {
                    reportError("Failed to parse " + fileURI + " due to the error: " + e.getMessage(), failFast);
                }

                try {
                    appendIncludes(fileURI, links, iih.getHrefs());
                } catch (TransformerException e) {
                    reportError(e.getMessageAndLocation(), failFast);
                }
            }

            List topStylesheets = getTopStylesheets(links);
            compileStylesheets(topStylesheets, failFast);
        } catch (VerificationFailedException e) {
            return false;
        }

        return numberOfErrors == 0;
    }

    private void compileStylesheets(List topStylesheets, boolean failFast) {
        TransformerFactory trFactory = TransformerFactory.newInstance();
        trFactory.setURIResolver(resolver);

        Iterator it = topStylesheets.iterator();
        while (it.hasNext()) {
            Source src = (Source) it.next();
            trace("Compiling stylesheet: " + src.getSystemId() + " ... ");
            VerifierErrorListener errorListener = new VerifierErrorListener();
            try {
                trFactory.setErrorListener(errorListener);
                trFactory.newTransformer(src);
                printErrorListenerErrors(errorListener, failFast);
            } catch (TransformerConfigurationException e) {
                printErrorListenerErrors(errorListener, failFast);
            }
        }
    }

    private void printErrorListenerErrors(VerifierErrorListener errorListener, boolean failFast) {
        Iterator errorsIt = errorListener.getCollectedErrors().iterator();
        while (errorsIt.hasNext()) {
            VerifierErrorListener.Error e = (VerifierErrorListener.Error) errorsIt.next();
            if (e.isWarning())
                reportWarning(e.getException().getMessageAndLocation());
            else
                reportError(e.getException().getMessageAndLocation(), false);
        }

        if (errorListener.wereErrors() && failFast)
            throw new VerificationFailedException();
    }

    private List getTopStylesheets(Map links) {
        List result = new ArrayList(20);
        Iterator it = links.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            StylesheetInfo sinfo = (StylesheetInfo) me.getValue();
            if (sinfo.referencesCounter == 0) {
                String systemId = sinfo.resolvedSource.getSystemId();
                try {
                    URI uri = new URI(systemId);
                    if (urisToVerify.contains(uri.normalize()))
                        result.add(sinfo.resolvedSource);
                } catch (URISyntaxException e) {
                    trace("Invalid URI: " + systemId);
                }
            }
        }

        return result;
    }

    private void appendIncludes(URI fileURI, Map links, Set hrefs) throws TransformerException {
        Iterator it = hrefs.iterator();
        while (it.hasNext()) {
            String href = (String) it.next();
            Source resolvedSource = resolver.resolve(href, fileURI.toString());
            String resolvedSystemId = resolvedSource.getSystemId();
            assert resolvedSystemId != null;
            try {
                URI resolvedURI = new URI(resolvedSystemId);
                registerStylesheet(resolvedURI, resolvedSource, links);
            } catch (URISyntaxException e) {
                trace("Invalid URI: " + resolvedSystemId);
            }
        }
    }

    private void registerStylesheet(URI uri, Source src, Map links) {
        if (!links.containsKey(uri)) {
            links.put(uri, new StylesheetInfo(src, 0));
        }
        else {
            StylesheetInfo sinfo = (StylesheetInfo) links.get(uri);
            sinfo.referencesCounter++;
        }
    }

    private void reportError(String message, boolean fail) {
        assert er != null;
        er.error(message);
        numberOfErrors++;
        if (fail)
            throw new VerificationFailedException();
    }

    private void reportWarning(String message) {
        assert er != null;
        er.warning(message);
    }

    private void trace(String message) {
        assert er != null;
        er.trace(message);
    }

    class StylesheetInfo {
        public Source resolvedSource;
        public int referencesCounter;

        public StylesheetInfo(Source resolverSource, int referencesCounter) {
            this.resolvedSource = resolverSource;
            this.referencesCounter = referencesCounter;
        }
    }
}
