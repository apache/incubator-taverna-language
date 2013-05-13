package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import org.junit.Test;

public class TestFileSystemProvider {

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
	public void createFSfromZip() throws Exception {
		Path path = Files.createTempFile("test", null);
		Files.delete(path);
		BundleFileSystemProvider.createBundleAsZip(path, null);
		assertTrue(Files.exists(path));
	}

}
