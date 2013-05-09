package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestFileSystemProvider {

	@Test
	public void installedProviders() throws Exception {
		for (FileSystemProvider provider : FileSystemProvider
				.installedProviders()) {
			if (provider instanceof BundleFileSystemProvider) {
				return;
			}
		}
		fail("Could not find BundleFileSystemProvider as installed provider");
	}

	@Test
	public void newByURI() throws Exception {
		Bundle bundle = Bundles.createBundle();
		
		URI w = new URI("widget", bundle.getSource().toUri().toASCIIString(), null);
		FileSystem fs = FileSystems.newFileSystem(w, 
				Collections.<String,Object>emptyMap());
		assertTrue(fs instanceof BundleFileSystem);
	}

}
