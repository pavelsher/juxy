package org.tigris.juxy.verifier;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.MatchingTask;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * $Id: VerifierTask.java,v 1.2 2005-09-02 08:19:52 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class VerifierTask extends MatchingTask implements ErrorReporter {
    private boolean failFast = false;

    public void execute() throws BuildException {
        List files = findFiles();
        Verifier verifier = new VerifierImpl();
        verifier.setFiles(files);
        verifier.setErrorReporter(this);
        if (!verifier.verify(failFast)) {
            throw new BuildException("Verification failed");
        }
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
        this.failFast = failOnError;
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

    public void debug(String message) {
        log(message + "\n");
    }

    public void error(String message) {
        log("ERROR: " + message + "\n");
    }

    public void warning(String message) {
        log("WARNING: " + message + "\n");
    }
}
