package org.purl.wf4ever.robundle.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.purl.wf4ever.robundle.Bundles;

public class Helper {
    protected BundleFileSystem fs;

    @Before
    public void makeFS() throws IOException {
        fs = BundleFileSystemProvider.newFileSystemFromTemporary();
    }

    @After
    public void closeAndDeleteFS() throws IOException {
        fs.close();
        Path source = fs.getSource();
        Files.deleteIfExists(source);
        if (source.getParent().getFileName().toString().startsWith("robundle")) {
            Bundles.deleteRecursively(source.getParent());
        }
    }
}
