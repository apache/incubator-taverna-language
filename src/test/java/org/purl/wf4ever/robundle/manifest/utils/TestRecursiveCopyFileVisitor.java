package org.purl.wf4ever.robundle.manifest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundles;

public class TestRecursiveCopyFileVisitor {


    @Test(expected=FileAlreadyExistsException.class)
    public void copyRecursivelyAlreadyExists() throws Exception {
        Path orig = Files.createTempDirectory("orig");
        Path dest = Files.createTempDirectory("dest");
        Bundles.copyRecursively(orig, dest);
    }

    @Test
    public void copyRecursivelyReplace() throws Exception {
        Path orig = Files.createTempDirectory("orig");
        Files.createFile(orig.resolve("file"));
        Path dest = Files.createTempDirectory("dest");
        Bundles.copyRecursively(orig, dest, StandardCopyOption.REPLACE_EXISTING);
        assertTrue(Files.isRegularFile(dest.resolve("file")));
        // Second copy should also be OK
        Bundles.copyRecursively(orig, dest, StandardCopyOption.REPLACE_EXISTING);
    }
    
    @Test
    public void copyRecursively() throws Exception {
        // TODO: Test NOFOLLOW and follow of symlinks
        
        Path orig = Files.createTempDirectory("orig");
        Files.createFile(orig.resolve("1"));
        Files.createDirectory(orig.resolve("2"));
        Files.createFile(orig.resolve("2/1"));
        Files.createDirectory(orig.resolve("2/2"));
        List<String> hello = Arrays.asList("Hello");

        Charset ascii = Charset.forName("ASCII");
        Files.write(orig.resolve("2/2/1"), hello, ascii);
        
        Files.createDirectory(orig.resolve("2/2/2"));
        Files.createFile(orig.resolve("3"));
        
        
        Path dest = Files.createTempDirectory("dest");
        Files.delete(dest);        
        Bundles.copyRecursively(orig, dest);
        
        assertTrue(Files.isDirectory(dest.resolve("2")));
        assertTrue(Files.isDirectory(dest.resolve("2/2")));
        assertTrue(Files.isDirectory(dest.resolve("2/2")));
        assertTrue(Files.isDirectory(dest.resolve("2/2/2")));
        assertTrue(Files.isRegularFile(dest.resolve("1")));
        assertTrue(Files.isRegularFile(dest.resolve("2/1")));
        assertTrue(Files.isRegularFile(dest.resolve("2/2/1")));
        assertTrue(Files.isRegularFile(dest.resolve("3")));
        assertEquals(hello, Files.readAllLines(dest.resolve("2/2/1"), ascii));        
    }
}
