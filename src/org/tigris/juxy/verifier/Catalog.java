package org.tigris.juxy.verifier;

/**
 * $Id: Catalog.java,v 1.1 2005-09-05 17:37:37 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class Catalog {
    private String catalogFiles;

    public void setCatalogFiles(String filePaths) {
        catalogFiles = filePaths;
    }

    public String getCatalogFiles() {
        return catalogFiles;
    }
}
