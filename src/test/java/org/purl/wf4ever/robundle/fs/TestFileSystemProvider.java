package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestFileSystemProvider {

    @Test
    public void getInstance() throws Exception {
        assertSame(BundleFileSystemProvider.getInstance(), BundleFileSystemProvider.getInstance());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void getInstanceEquals() throws Exception {
        assertEquals(BundleFileSystemProvider.getInstance(),
                new BundleFileSystemProvider());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void getInstanceHashCode() throws Exception {
        assertEquals(BundleFileSystemProvider.getInstance().hashCode(),
                new BundleFileSystemProvider().hashCode());
    }
    
    @SuppressWarnings({ "deprecation", "static-access" })
    @Test
    public void sameOpen() throws Exception {
        assertSame(BundleFileSystemProvider.getInstance().openFilesystems,
                new BundleFileSystemProvider().openFilesystems);
    }
    
	@Test
	public void installedProviders() throws Exception {
		for (FileSystemProvider provider : FileSystemProvider
				.installedProviders()) {
			if (provider instanceof BundleFileSystemProvider) {
				assertSame(provider, BundleFileSystemProvider.getInstance());
				return;
			}
		}
		fail("Could not find BundleFileSystemProvider as installed provider");
	}

	@Test
	public void newByURI() throws Exception {

		Path path = Files.createTempFile("test", "zip");
		path.toFile().deleteOnExit();
		BundleFileSystemProvider.createBundleAsZip(path, null);

		// HACK: Use a opaque version of widget: with the file URI as scheme
		// specific part
		URI w = new URI("widget", path.toUri().toASCIIString(), null);
		try (FileSystem fs = FileSystems.newFileSystem(w,
				Collections.<String, Object> emptyMap())) {
		    assertTrue(fs instanceof BundleFileSystem);
		}
	}

    @Test
    public void bundleWithSpaces() throws Exception {
        Path path = Files.createTempFile("with several spaces", ".zip");
        path.toFile().deleteOnExit();
        Files.delete(path);
        
        // Will fail with FileSystemNotFoundException without env:
        //FileSystems.newFileSystem(path, null);
        
        // Neither does this work, as it does not double-escape:
        // URI jar = URI.create("jar:" + path.toUri().toASCIIString());                

        URI widget = new URI("widget", path.toUri().toString(), null);
        assertTrue(widget.toASCIIString().contains("with%2520several%2520spaces"));
        
        Map<String, Object> env = new HashMap<>();
        env.put("create", "true");
 
        try (FileSystem fs = FileSystems.newFileSystem(widget, env)) {
        } 
        assertTrue(Files.exists(path));
        // Reopen from now-existing Path to check that the URI is
        // escaped in the same way
        try (FileSystem fs = BundleFileSystemProvider.newFileSystemFromExisting(path)) {
        }        
    }
    
    @Test
    public void bundleWithUnicode() throws Exception {
        Path path = Files.createTempFile("with\u2301unicode\u263bhere", ".zip");
        path.toFile().deleteOnExit();
        Files.delete(path);
        //System.out.println(path); // Should contain a electrical symbol and smiley
        URI widget = new URI("widget", path.toUri().toString(), null);
        // FIXME: The below passes on Windows 8 but not in Linux!?
        //System.out.println(widget);
        //assertTrue(widget.toString().contains("\u2301"));
        //assertTrue(widget.toString().contains("\u263b"));        
        
        Map<String, Object> env = new HashMap<>();
        env.put("create", "true");
 
        try (FileSystem fs = FileSystems.newFileSystem(widget, env)) {     
        }
        assertTrue(Files.exists(path));
        // Reopen from now-existing Path to check that the URI is
        // escaped in the same way
        try (FileSystem fs = BundleFileSystemProvider.newFileSystemFromExisting(path)) {
        }
    }

	
	@Test
	public void newFileSystemFromExisting() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		BundleFileSystemProvider.createBundleAsZip(path, "application/x-test");
		assertTrue(Files.exists(path));
		try (BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromExisting(path)) {
    		assertEquals(path, f.getSource());
    		assertEquals("application/x-test", Files.readAllLines(
    				f.getRootDirectory().resolve("mimetype"), 
    				Charset.forName("ASCII")).get(0));
		}
	}
	
	@Test
	public void newFileSystemFromNewDefaultMime() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromNew(path);
		assertTrue(Files.exists(path));
		assertEquals(path, f.getSource());
		assertEquals("application/vnd.wf4ever.robundle+zip", Files.readAllLines(
				f.getRootDirectory().resolve("mimetype"), 
				Charset.forName("ASCII")).get(0));
	}
	
	@Test
	public void newFileSystemFromNew() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromNew(path, "application/x-test2");
		assertTrue(Files.exists(path));
		assertEquals(path, f.getSource());
		assertEquals("application/x-test2", Files.readAllLines(
				f.getRootDirectory().resolve("mimetype"), 
				Charset.forName("ASCII")).get(0));
	}
	
	@Test
	public void newFileSystemFromTemporary() throws Exception {
	    Path source;
		try (BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromTemporary()) {		
    		source = f.getSource();
            assertTrue(Files.exists(source));
    		assertEquals("application/vnd.wf4ever.robundle+zip", Files.readAllLines(
    				f.getRootDirectory().resolve("mimetype"), 
    				Charset.forName("ASCII")).get(0));
		}
		Files.delete(source);
	}
	

}
