package org.purl.wf4ever.robundle;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.purl.wf4ever.robundle.fs.BundleFileSystem;

public class Bundle implements Closeable {

    private boolean deleteOnClose;
    private final Path root;

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
        getFileSystem().close();
        if (deleteOnClose) {
            Files.deleteIfExists(getSource());
        }
    }

    public FileSystem getFileSystem() {
        return getRoot().getFileSystem();
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

}
