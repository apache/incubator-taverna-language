package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;

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
		URI widgetUri = URI
				.create("widget://2be0ea48-4b43-4be7-be76-8859f8414296/");
		FileSystem fs = FileSystems.newFileSystem(widgetUri,
				new HashMap<String, Object>());
		assertTrue(fs instanceof BundleFileSystem);
	}

	@Test
	public void fromFile() throws Exception {
		Bundle bundle = Bundles.createBundle();
		FileSystem fs = FileSystems.newFileSystem(bundle.getSource(),
				getClass().getClassLoader());
		assertTrue(fs instanceof BundleFileSystem);
	}

}
