package org.tigris.juxy.verifier;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.MatchingTask;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * $Id: VerifierTask.java,v 1.1 2005-08-30 19:51:19 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class VerifierTask extends MatchingTask implements ErrorReporter {

    public void execute() throws BuildException {
        verifyRequiredParams();
        List files = findFiles();
        Verifier verifier = new VerifierImpl();
        verifier.setFiles(files);
        verifier.setErrorReporter(this);
        if (!verifier.verify(false)) {
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

    private void verifyRequiredParams() {

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

    public void trace(String message) {
        log(message + "\n");
    }

    public void error(String message) {
        log("ERROR: " + message + "\n");
    }

    public void warning(String message) {
        log("WARNING: " + message + "\n");
    }
}
