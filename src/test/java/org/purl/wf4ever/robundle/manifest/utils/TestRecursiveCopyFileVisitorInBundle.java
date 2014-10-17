package org.purl.wf4ever.robundle.manifest.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestRecursiveCopyFileVisitorInBundle extends
		TestRecursiveCopyFileVisitor {

	private Bundle bundle;

	@Before
	public void createBundle() throws IOException {
		bundle = Bundles.createBundle();
	}

	@After
	public void closeBundle() throws IOException {
		if (bundle != null) {
			bundle.close();
		}
		bundle = null;
	}

	@Override
	protected Path tempDir(String name) throws IOException {
		return Files.createTempDirectory(bundle.getRoot(), name);
	}
}
