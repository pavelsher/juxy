package org.tigris.juxy.verifier;

/**
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
