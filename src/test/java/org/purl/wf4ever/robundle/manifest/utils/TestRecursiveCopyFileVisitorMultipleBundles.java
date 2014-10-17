package org.purl.wf4ever.robundle.manifest.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestRecursiveCopyFileVisitorMultipleBundles extends
		TestRecursiveCopyFileVisitor {

	private List<Bundle> bundles = new ArrayList<>();

	@After
	public void closeBundle() throws IOException {
		for (Bundle b : bundles) {
			b.close();
		}
	}

	@Override
	protected Path tempDir(String name) throws IOException {
		Bundle bundle = Bundles.createBundle();
		bundles.add(bundle);
		return Files.createTempDirectory(bundle.getRoot(), name);
	}
}
