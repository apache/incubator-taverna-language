package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBundleFileSystem {

    private BundleFileSystem fs;

    @Before
    public void newFS() throws Exception {
        fs = BundleFileSystemProvider.newFileSystemFromTemporary();
        // System.out.println(fs.getSource());
    }

    @After
    public void closeFS() throws IOException {
        fs.close();
        Files.deleteIfExists(fs.getSource());
    }

    /**
     * Test that BundleFileSystem does not allow a ZIP file to also be a folder.
     * See http://stackoverflow.com/questions/16588321/
     * 
     * @throws Exception
     */
    @Test
    public void directoryAndFile() throws Exception {
        Path folder = fs.getPath("folder");
        assertFalse(Files.exists(folder));
        Files.createFile(folder);
        assertTrue(Files.exists(folder));
        assertTrue(Files.isRegularFile(folder));
        assertFalse(Files.isDirectory(folder));

        try {
            Files.createDirectory(folder);
            // Disable for now, just to see where this leads
            fail("Should have thrown FileAlreadyExistsException");
        } catch (FileAlreadyExistsException ex) {
        }

        try {
            Files.createDirectories(folder);
            fail("Should have thrown FileAlreadyExistsException");
        } catch (FileAlreadyExistsException ex) {
        }

        Path child = folder.resolve("child");

        try {
            Files.createFile(child);
            fail("Should have thrown NotDirectoryException");
        } catch (NotDirectoryException ex) {
        }

        assertTrue(Files.isRegularFile(folder));
        assertFalse(Files.isDirectory(fs.getPath("folder/")));
    }
}
