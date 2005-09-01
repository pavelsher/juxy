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
    private int numberOfVerifiedFiles = 0;
    private int numberOfFilesToVerify = 0;

    public void setErrorReporter(ErrorReporter er) {
        assert er != null;
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
            if (f.exists() && f.isFile())
                urisToVerify.add(f.toURI().normalize());
        }
    }

    public boolean verify(boolean failFast) {
        try {
            Map stylesheets = new HashMap();
            IncludeInstructionsHandler iih = new IncludeInstructionsHandler();
            XMLReader reader = SAXUtil.newXMLReader();
            reader.setContentHandler(iih);

            Iterator it = urisToVerify.iterator();
            while (it.hasNext()) {
                URI fileURI = (URI) it.next();
                boolean parsed = false;
                Source src = new StreamSource(fileURI.toString());
                registerStylesheet(fileURI, src, stylesheets);
                iih.reset();
                try {
                    reader.parse(fileURI.toString());
                    parsed = true;
                } catch (IOException e) {
                    reportError("Failed to parse " + fileURI + " due to error: " + e.getMessage(), failFast);
                } catch (ParseStoppedException e) {
                    // parsing stopped by handler
                    parsed = true;
                } catch (SAXException e) {
                    reportError("Failed to parse " + fileURI + " due to error: " + e.getMessage(), failFast);
                }

                if (!parsed) {
                    urisToVerify.remove(fileURI);
                } else {
                    try {
                        processIncludes(fileURI, stylesheets, iih.getHrefs());
                    } catch (TransformerException e) {
                        urisToVerify.remove(fileURI);
                        reportError(e.getMessageAndLocation(), failFast);
                    }
                }
            }

            List topStylesheets = getTopStylesheets(stylesheets);
            numberOfFilesToVerify = topStylesheets.size();
            debug("Found " + topStylesheets.size() + " stylesheet(s) to verify");
            verifyStylesheets(topStylesheets, failFast);
        } catch (VerificationFailedException e) {
            return false;
        }

        return numberOfErrors == 0;
    }

    public int getNumberOfVerifiedFiles() {
        return numberOfVerifiedFiles;
    }

    public int getNumberOfFilesToVerify() {
        return numberOfFilesToVerify;
    }

    private void verifyStylesheets(List topStylesheets, boolean failFast) {
        TransformerFactory trFactory = TransformerFactory.newInstance();
        trFactory.setURIResolver(resolver);

        Iterator it = topStylesheets.iterator();
        while (it.hasNext()) {
            Source src = (Source) it.next();
            debug("Verifying stylesheet: " + src.getSystemId() + " ... ");
            VerifierErrorListener errorListener = new VerifierErrorListener();
            try {
                trFactory.setErrorListener(errorListener);
                trFactory.newTransformer(src);
                if (!errorListener.wereErrors())
                    numberOfVerifiedFiles++;
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
            try {
                URI uri = new URI(sinfo.resolvedSource.getSystemId());
                if (sinfo.referencesCounter == 0 && urisToVerify.contains(uri))
                    result.add(sinfo.resolvedSource);
            } catch (URISyntaxException e) {
                debug("Invalid URI: " + sinfo.resolvedSource.getSystemId());
            }
        }

        return result;
    }

    private void processIncludes(URI fileURI, Map links, Set hrefs) throws TransformerException {
        Iterator it = hrefs.iterator();
        while (it.hasNext()) {
            String href = (String) it.next();
            Source resolvedSource = resolver.resolve(href, fileURI.toString());
            String resolvedSystemId = resolvedSource.getSystemId();
            assert resolvedSystemId != null;
            try {
                URI resolvedURI = new URI(resolvedSystemId);
                if (!links.containsKey(resolvedURI))
                    registerStylesheet(resolvedURI, resolvedSource, links);

                incrementReferences(links, resolvedURI);
            } catch (URISyntaxException e) {
                debug("Invalid URI: " + resolvedSystemId);
            }
        }
    }

    private void registerStylesheet(URI uri, Source src, Map links) {
        if (!links.containsKey(uri))
            links.put(uri, new StylesheetInfo(src, 0));
        else
            incrementReferences(links, uri);
    }

    private void incrementReferences(Map links, URI uri) {
        StylesheetInfo sinfo = (StylesheetInfo) links.get(uri);
        sinfo.referencesCounter++;
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

    private void debug(String message) {
        assert er != null;
        er.debug(message);
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
