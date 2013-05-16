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
		BundleFileSystemProvider.createBundleAsZip(path, null);

		// HACK: Use a opaque version of widget: with the file URI as scheme
		// specific part
		URI w = new URI("widget", path.toUri().toASCIIString(), null);
		FileSystem fs = FileSystems.newFileSystem(w,
				Collections.<String, Object> emptyMap());
		assertTrue(fs instanceof BundleFileSystem);
	}

	@Test
	public void newFileSystemFromExisting() throws Exception {
		Path path = Files.createTempFile("test", null);
		Files.delete(path);
		BundleFileSystemProvider.createBundleAsZip(path, "application/x-test");
		assertTrue(Files.exists(path));
		BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromExisting(path);
		assertEquals(path, f.getSource());
		assertEquals("application/x-test", Files.readAllLines(
				f.getRootDirectory().resolve("mimetype"), 
				Charset.forName("ASCII")).get(0));
	}
	
	@Test
	public void newFileSystemFromNewDefaultMime() throws Exception {
		Path path = Files.createTempFile("test", null);
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
		BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromTemporary();
		assertTrue(Files.exists(f.getSource()));
		assertEquals("application/vnd.wf4ever.robundle+zip", Files.readAllLines(
				f.getRootDirectory().resolve("mimetype"), 
				Charset.forName("ASCII")).get(0));
	}
	

}
