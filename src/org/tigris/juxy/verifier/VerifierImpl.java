package org.tigris.juxy.verifier;

import org.tigris.juxy.util.ExceptionUtil;
import org.tigris.juxy.util.JuxyURIResolver;
import org.tigris.juxy.util.SAXUtil;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 */
public class VerifierImpl implements Verifier {
  private List urisToVerify = new ArrayList(20);
  private ErrorReporter er;
  private URIResolver resolver = new JuxyURIResolver();
  private int numberOfVerifiedFiles = 0;
  private int numberOfNotVerifiedFiles = 0;
  private boolean wereParseErrors = false;
  private String transformerFactoryClassName;

  public void setErrorReporter(ErrorReporter er) {
    assert er != null;
    this.er = er;
  }

  public void setTransformerFactory(String className) {
    assert className != null && className.length() > 0;
    this.transformerFactoryClassName = className;
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
        try {
          urisToVerify.add(f.getCanonicalFile().toURI().normalize());
        } catch (IOException e) {
          info("File " + f.getAbsolutePath() + " won't be verified: " + e.toString());
        }
    }
  }

  public boolean verify(boolean failOnError) {
    info("Searching for stylesheets to verify ...");
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
      ErrorsCollector ec = new ErrorsCollector();
      try {
        reader.setErrorHandler(ec);
        reader.parse(fileURI.toString());
        parsed = true;
      } catch (IOException e) {
        reportError("Failed to parse " + fileURI + " due to error: " + e.getMessage());
      } catch (ParseStoppedException e) {
        // parsing stopped by handler
        parsed = true;
      } catch (SAXException e) {
      } finally {
        if (ec.hasErrors()) {
          reportError("Failed to parse file " + fileURI);
          reportParserErrors(ec.getParseErrors());
          reportParserWarnings(ec.getParseWarnings());
        } else if (ec.hasWarnings()) {
          reportWarning("There were warnings while parsing file " + fileURI);
          reportParserWarnings(ec.getParseWarnings());
        }
      }

      if (!parsed || ec.hasErrors()) {
        it.remove();
        wereParseErrors = true;
        if (failOnError)
          return false;
      } else {
        processIncludes(fileURI, stylesheets, iih.getHrefs());
      }
    }

    List topStylesheets = getTopStylesheets(stylesheets);
    info(topStylesheets.size() + " stylesheet(s) were selected for verification");
    verifyStylesheets(topStylesheets, failOnError);

    return numberOfNotVerifiedFiles == 0 && !wereParseErrors;
  }

  private void reportParserWarnings(SAXParseException[] warnings) {
    for (int i = 0; i < warnings.length; i++)
      reportWarning(ExceptionUtil.exceptionToString(warnings[i], false));
  }

  private void reportParserErrors(SAXParseException[] errors) {
    for (int i = 0; i < errors.length; i++)
      reportError(ExceptionUtil.exceptionToString(errors[i], false));
  }

  public int getNumberOfVerifiedFiles() {
    return numberOfVerifiedFiles;
  }

  public int getNumberOfNotVerifiedFiles() {
    return numberOfNotVerifiedFiles;
  }

  private void verifyStylesheets(List topStylesheets, boolean failOnError) {
    Collections.sort(topStylesheets, new SourceComparator());

    TransformerFactory trFactory = getTransformerFactory();
    info("Obtained TransformerFactory: " + trFactory.getClass().getName());

    trFactory.setURIResolver(resolver);

    info("Verifying found files:");
    Iterator it = topStylesheets.iterator();
    while (it.hasNext()) {
      Source src = (Source) it.next();
      info(calculateRelativePath(src.getSystemId()) + " ...");
      boolean verified = false;
      ErrorsCollector errorListener = new ErrorsCollector();
      try {
        trFactory.setErrorListener(errorListener);
        trFactory.newTransformer(src);
        if (!errorListener.hasErrors())
          verified = true;
      } catch (TransformerConfigurationException e) {
        e.printStackTrace();
        //reportTransformerErrors(new TransformerException[] {e});
      } finally {
        if (errorListener.hasErrors()) {
          reportTransformerErrors(errorListener.getTransformErrors());
          reportTransformerWarnings(errorListener.getTransformWarnings());
        } else if (errorListener.hasWarnings()) {
          reportTransformerWarnings(errorListener.getTransformWarnings());
        }
      }

      if (!verified) {
        numberOfNotVerifiedFiles++;
        if (failOnError)
          throw new VerificationFailedException();
      } else {
        numberOfVerifiedFiles++;
      }
    }
  }

  private String calculateRelativePath(String systemId) {
    String userDir = System.getProperty("user.dir");
    try {
      return new File(userDir).getCanonicalFile().toURI().relativize(new URI(systemId)).toString();
    } catch (URISyntaxException e) {
      return systemId;
    } catch (IOException e) {
      return systemId;
    }
  }

  private TransformerFactory getTransformerFactory() {
    if (transformerFactoryClassName != null) {
      try {
        Class factoryClass = Class.forName(transformerFactoryClassName);
        if (factoryClass != null)
          return (TransformerFactory) factoryClass.newInstance();

      } catch (ClassNotFoundException e) {
        reportWarning("Failed to load class for specified TransformerFactory: " + transformerFactoryClassName);
      } catch (IllegalAccessException e) {
        reportWarning("Failed to instantiate class for specified TransformerFactory: " + transformerFactoryClassName);
      } catch (InstantiationException e) {
        reportWarning("Failed to instantiate class for specified TransformerFactory: " + transformerFactoryClassName);
      }
    }

    info("Using default TransformerFactory");
    return TransformerFactory.newInstance();
  }

  private void reportTransformerWarnings(TransformerException[] warnings) {
    for (int i = 0; i < warnings.length; i++)
      reportWarning(ExceptionUtil.exceptionToString(warnings[i], false));
  }

  private void reportTransformerErrors(TransformerException[] errors) {
    for (int i = 0; i < errors.length; i++)
      reportError(ExceptionUtil.exceptionToString(errors[i], false));
  }

  private List getTopStylesheets(Map links) {
    List result = new ArrayList(20);
    Iterator it = links.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry) it.next();
      StylesheetInfo sinfo = (StylesheetInfo) me.getValue();
      try {
        URI uri = new URI(sinfo.resolvedSource.getSystemId());
        if (sinfo.referencesCounter == 0 && urisToVerify.contains(uri))
          result.add(sinfo.resolvedSource);
      } catch (URISyntaxException e) {
        info("Invalid URI: " + sinfo.resolvedSource.getSystemId());
      }
    }

    return result;
  }

  private void processIncludes(URI fileURI, Map links, Set hrefs) {
    Iterator it = hrefs.iterator();
    while (it.hasNext()) {
      String href = (String) it.next();
      String resolvedSystemId = "";
      try {
        Source resolvedSource = resolver.resolve(href, fileURI.toString());
        if (resolvedSource != null) {
          resolvedSystemId = resolvedSource.getSystemId();
          assert resolvedSystemId != null;
          URI resolvedURI = new URI(resolvedSystemId);
          if (!links.containsKey(resolvedURI))
            registerStylesheet(resolvedURI, resolvedSource, links);

          incrementReferences(links, resolvedURI);
        } else {
          reportWarning("Failed to resolve URI: " + href);
        }
      } catch (URISyntaxException e) {
        reportWarning("Invalid URI: " + resolvedSystemId);
      } catch (TransformerException e) {
        reportWarning("Failed to resolve URI: " + href);
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

  private void reportError(String message) {
    assert er != null;
    er.error(message);
  }

  private void reportWarning(String message) {
    assert er != null;
    er.warning(message);
  }

  private void info(String message) {
    assert er != null;
    er.info(message);
  }

  class SourceComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      Source s1 = (Source) o1;
      Source s2 = (Source) o2;
      return s1.getSystemId().compareTo(s2.getSystemId());
    }
  }

  class StylesheetInfo {
    public Source resolvedSource;
    public int referencesCounter;

    public StylesheetInfo(Source resolverSource, int referencesCounter) {
      this.resolvedSource = resolverSource;
      this.referencesCounter = referencesCounter;
    }
  }

  public static class ErrorsCollector implements ErrorHandler, ErrorListener {
    private List errors = new ArrayList();
    private List warnings = new ArrayList();

    public void warning(SAXParseException exception) throws SAXException {
      warnings.add(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
      errors.add(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
      errors.add(exception);
    }

    public SAXParseException[] getParseErrors() {
      return (SAXParseException[]) errors.toArray(new SAXParseException[]{});
    }

    public SAXParseException[] getParseWarnings() {
      return (SAXParseException[]) warnings.toArray(new SAXParseException[]{});
    }

    public boolean hasErrors() {
      return errors.size() > 0;
    }

    public boolean hasWarnings() {
      return warnings.size() > 0;
    }

    public void warning(TransformerException exception) throws TransformerException {
      warnings.add(exception);
    }

    public void error(TransformerException exception) throws TransformerException {
      errors.add(exception);
    }

    public void fatalError(TransformerException exception) throws TransformerException {
      errors.add(exception);
    }

    public TransformerException[] getTransformErrors() {
      return (TransformerException[]) errors.toArray(new TransformerException[]{});
    }

    public TransformerException[] getTransformWarnings() {
      return (TransformerException[]) warnings.toArray(new TransformerException[]{});
    }
  }
}
