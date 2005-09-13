package org.tigris.juxy.verifier;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.tigris.juxy.Version;

import javax.xml.transform.URIResolver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * $Id: VerifierTask.java,v 1.8 2005-09-13 08:32:03 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class VerifierTask extends MatchingTask implements ErrorReporter {
    private boolean failOnError = true;
    private Catalog catalog;
    private Factory factory;

    public void execute() throws BuildException {
        info("XSLT Verifier version " + Version.VERSION + " by Pavel Sher (pavelsher@tigris.org)");
        List files = findFiles();
        Verifier verifier = new VerifierImpl();
        verifier.setFiles(files);
        verifier.setErrorReporter(this);
        if (catalog != null)
            verifier.setURIResolver(createCatalogResolver());
        if (factory != null)
            verifier.setTransformerFactory(factory.getFactoryClassName());

        if (!verifier.verify(failOnError) && failOnError)
            throw new BuildException("Verification failed");

        int notVerifiedNum = verifier.getNumberOfNotVerifierFiles();
        if (notVerifiedNum > 0)
            info(notVerifiedNum + " stylesheet(s) were not verified due to errors");
    }

    private URIResolver createCatalogResolver() {
        String catalogs = catalog.getCatalogFiles();
        CatalogManager cm = CatalogManager.getStaticManager();
        cm.setCatalogFiles(toResolverFileList(catalogs));
        cm.setIgnoreMissingProperties(true);
        //cm.setVerbosity(10);

        return new CatalogResolver(cm);
    }

    private String toResolverFileList(String catalogs) {
        StringBuffer catalogFiles = new StringBuffer(100);
        StringTokenizer st = new StringTokenizer(catalogs, ",");
        while (st.hasMoreTokens()) {
            catalogFiles.append(st.nextToken().trim()).append(";");
        }
        return catalogFiles.toString();
    }

    private List findFiles() {
        List files = new ArrayList(20);
        DirectoryScanner scanner = getDirectoryScanner();
        scanner.scan();
        String[] filesPaths = scanner.getIncludedFiles();
        for(int i=0; i<filesPaths.length; i++) {
            files.add(new File(scanner.getBasedir(), filesPaths[i]));
        }

        return files;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    private DirectoryScanner getDirectoryScanner() {
        return fileset.getDirectoryScanner(getProject());
    }

    public void setDir(String dir) {
        this.fileset.setDir(new File(getProject().getBaseDir(), dir));
    }

    public void addFileSet(FileSet fs) {
        this.fileset = fs;
    }

    public void addConfiguredCatalog(Catalog catalog) {
        if (catalog.getCatalogFiles() == null || catalog.getCatalogFiles().length() == 0)
            throw new BuildException("Attribute catalogfiles is required for catalog");

        this.catalog = catalog;
    }

    public void addConfiguredFactory(Factory factory) {
        if (factory.getFactoryClassName() == null || factory.getFactoryClassName().length() == 0)
            throw new BuildException("Attribute name is required for factory");
        this.factory = factory;
    }

    public void info(String message) {
        log(message);
    }

    public void error(String message) {
        log("ERROR: " + message);
    }

    public void warning(String message) {
        log("WARNING: " + message);
    }
}
