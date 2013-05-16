package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

public class TestZipFS {

    private static Path zip;

    /**
     * Verifies http://stackoverflow.com/questions/16588321/ as both ZIP format
     * and Java 7 ZIPFS allows a folder and file to have the same name.
     * 
     */
    @Test
    public void directoryOrFile() throws Exception {
        try (FileSystem fs = tempZipFS()) {
            Path folder = fs.getPath("folder");
            assertFalse(Files.exists(folder));
            Files.createFile(folder);
            assertTrue(Files.exists(folder));
            assertTrue(Files.isRegularFile(folder));
            assertFalse(Files.isDirectory(folder));

            try {
                Files.createDirectory(folder);

                // Disable for now, just to see where this leads
                // fail("Should have thrown FileAlreadyExistsException");
            } catch (FileAlreadyExistsException ex) {
            }

            // For some reason the second createDirectory() fails correctly
            try {
                Files.createDirectory(folder);
                fail("Should have thrown FileAlreadyExistsException");
            } catch (FileAlreadyExistsException ex) {
            }

            Path child = folder.resolve("child");
            Files.createFile(child);

            // Look, it's both a file and folder!
            // Can this be asserted?
            assertTrue(Files.isRegularFile(folder));
            // Yes, if you include the final /
            assertTrue(Files.isDirectory(fs.getPath("folder/")));
            // But not the parent
            // assertTrue(Files.isDirectory(child.getParent()));
            // Or the original Path
            // assertTrue(Files.isDirectory(folder));
        }
        // What if we open it again.. can we find both?
        try (FileSystem fs2 = FileSystems.newFileSystem(zip, null)) {
            assertTrue(Files.isRegularFile(fs2.getPath("folder")));
            assertTrue(Files.isRegularFile(fs2.getPath("folder/child")));
            assertTrue(Files.isDirectory(fs2.getPath("folder/")));

            // We can even list the folder
            try (DirectoryStream<Path> s = Files.newDirectoryStream(fs2
                    .getPath("folder/"))) {
                boolean found = false;
                for (Path p : s) {
                    found = p.endsWith("child");
                }
                assertTrue("Did not find 'child'", found);
            }
            // But if we list the root, do we find "folder" or "folder/"?
            Path root = fs2.getRootDirectories().iterator().next();
            try (DirectoryStream<Path> s = Files.newDirectoryStream(root)) {
                List<String> paths = new ArrayList<>();
                for (Path p : s) {
                    paths.add(p.toString());
                }
                // We find both!
                assertEquals(2, paths.size());
                assertTrue(paths.contains("/folder"));
                assertTrue(paths.contains("/folder/"));
            }
            // SO does that mean this is a feature, and not a bug?
            // See http://stackoverflow.com/questions/16588321/ for more
        }

    }

    public static FileSystem tempZipFS() throws Exception {
        zip = Files.createTempFile("test", ".zip");
        Files.delete(zip);
        System.out.println(zip);
        URI jar = new URI("jar", zip.toUri().toString(), null);
        Map<String, Object> env = new HashMap<>();
        env.put("create", "true");
        return FileSystems.newFileSystem(jar, env);
    }
    
    /* http://stackoverflow.com/a/16584723/412540 */
    @Test
    public void jarWithSpaces() throws Exception {
        Path path = Files.createTempFile("with several spaces", ".zip");
        Files.delete(path);

        // Will fail with FileSystemNotFoundException without env:
        //FileSystems.newFileSystem(path, null);

        // Neither does this work, as it does not double-escape:
        // URI jar = URI.create("jar:" + path.toUri().toASCIIString());                

        URI jar = new URI("jar", path.toUri().toString(), null);
        assertTrue(jar.toASCIIString().contains("with%2520several%2520spaces"));

        Map<String, Object> env = new HashMap<>();
        env.put("create", "true");

        try (FileSystem fs = FileSystems.newFileSystem(jar, env)) {
            URI root = fs.getPath("/").toUri();    
            assertTrue(root.toString().contains("with%2520several%2520spaces"));
        } 
        // Reopen from now-existing Path to check that the URI is
        // escaped in the same way
        try (FileSystem fs = FileSystems.newFileSystem(path, null)) {
            URI root = fs.getPath("/").toUri();
            //System.out.println(root.toASCIIString());
            assertTrue(root.toString().contains("with%2520several%2520spaces"));
        }
    }

    /**
     * See http://stackoverflow.com/a/14034572/412540 - spaces in filename seems
     * broken in Java 8. This test passes in Java 7 and should pass in Java 8
     * for regression testing.
     */
    @Test
    public void jarWithSpacesJava8() throws Exception {

        Path dir = Files.createTempDirectory("test");
        dir.resolve("test");

        Path path = dir.resolve("with several spaces.zip");

        // Make empty zip file - the old way!
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(
                path, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING))) {
            out.closeEntry();
        }

        Map<String, Object> env = new HashMap<>();

        URI root;
        // Open by path
        try (FileSystem fs = FileSystems.newFileSystem(path, null)) {
            // Works fine
            root = fs.getPath("/").toUri();
            //System.out.println(root.toASCIIString());

            // Double-escaped, as expected and compatible with Java 7
            assertTrue(root.toString().contains("with%2520several%2520spaces.zip")) ;
        }

        // Open it again from the URI
        try (FileSystem fs = FileSystems.newFileSystem(root, env)) {
            root = fs.getPath("/").toUri();
            //System.out.println(root.toASCIIString());
            assertTrue(root.toString().contains("with%2520several%2520spaces.zip"));
        }
        
        // What if we construct the JAR URI as in Java 7?
        URI jar = new URI("jar", path.toUri().toString(), null);
        try (FileSystem fs = FileSystems.newFileSystem(jar, env)) {
            root = fs.getPath("/").toUri();
            //System.out.println(root.toASCIIString());
            assertTrue(root.toString().contains("with%2520several%2520spaces.zip"));
        }

        // OK, let's just create one and see what we get
        env.put("create", "true");
        Files.delete(path);

        try (FileSystem fs = FileSystems.newFileSystem(jar, env)) {
            root = fs.getPath("/").toUri();
            //System.out.println(root.toASCIIString());
            assertTrue(root.toString().contains("with%2520several%2520spaces.zip"));
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                assertEquals("with several spaces.zip", file.getFileName().toString());
                // not with%20several%20spaces.zip
            }
        }

    }

}
