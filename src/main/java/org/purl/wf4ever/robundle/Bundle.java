package org.purl.wf4ever.robundle;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.purl.wf4ever.robundle.fs.BundleFileSystem;
import org.purl.wf4ever.robundle.manifest.Manifest;
import org.purl.wf4ever.robundle.manifest.RDFToManifest;
import org.purl.wf4ever.robundle.manifest.combine.CombineManifest;
import org.purl.wf4ever.robundle.manifest.odf.ODFManifest;

public class Bundle implements Closeable {

    private boolean deleteOnClose;
    private final Path root;
    private Manifest manifest;

    public Bundle(Path root, boolean deleteOnClose) {
        this.root = root;
        this.setDeleteOnClose(deleteOnClose);
    }

    @Override
    public void close() throws IOException {
        close(isDeleteOnClose());
    }

    protected void close(boolean deleteOnClose) throws IOException {
        if (! getFileSystem().isOpen()) {
            return;
        }

        if (! deleteOnClose) {	           
	        // update manifest
	        getManifest().populateFromBundle();
	        getManifest().writeAsJsonLD();
	        if (ODFManifest.containsManifest(this)) {
	        	getManifest().writeAsODFManifest();
	        }
	        if (CombineManifest.containsManifest(this)) {
	        	getManifest().writeAsCombineManifest();
	        }
        } else {
        	// FIXME: Enable this if closing temporary bundles is 
        	// slow doing closing (as those files are being compressed):
        	//RecursiveDeleteVisitor.deleteRecursively(getRoot());
        }
        getFileSystem().close();
        if (deleteOnClose) {
            Files.deleteIfExists(getSource());
        }
    }

    public FileSystem getFileSystem() {
        return getRoot().getFileSystem();
    }

    public Path getPath(String path) {
		return getRoot().resolve(path);
	}
    
    public Path getRoot() {
        return root;
    }

    public Path getSource() {
        BundleFileSystem fs = (BundleFileSystem) getFileSystem();
        return fs.getSource();
    }

    public boolean isDeleteOnClose() {
        return deleteOnClose;
    }

    public void setDeleteOnClose(boolean deleteOnClose) {
        this.deleteOnClose = deleteOnClose;
    }

    public Manifest getManifest() throws IOException {
        if (manifest == null) {
            synchronized(this) {
                if (manifest == null) {
                    manifest = readOrPopulateManifest();
                }
            }
        }
        return manifest;
    }

    protected Manifest readOrPopulateManifest() throws IOException {
        Manifest newManifest = new Manifest(this);                    
        Path manifestPath = Bundles.getManifestPath(this);        
        if (Files.exists(manifestPath)) { 
            try (InputStream manifestStream = Files.newInputStream(manifestPath)) { 
                new RDFToManifest().readTo(manifestStream, newManifest, manifestPath.toUri());                
            }
            // TODO: Also support reading manifest.rdf?        
        } else if (ODFManifest.containsManifest(this) ){
        	new ODFManifest(newManifest).readManifestXML();
        } else if (CombineManifest.containsManifest(this)){
        	new CombineManifest(newManifest).readCombineArchive();
        } else {
            // Fallback (might be a fresh or 3rd party bundle), populate from zip content
            newManifest.populateFromBundle();
        }
        return newManifest;
    }

	


}
