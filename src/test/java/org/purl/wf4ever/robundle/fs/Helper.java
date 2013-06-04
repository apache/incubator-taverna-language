package org.purl.wf4ever.robundle.fs;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;

public class Helper {
    protected BundleFileSystem fs;

    @Before
    public void makeFS() throws IOException {
        fs = BundleFileSystemProvider.newFileSystemFromTemporary();
    }

    @After
    public void closeAndDeleteFS() throws IOException {
        fs.close();
        Files.deleteIfExists(fs.getSource());
    }
}
