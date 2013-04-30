package uk.org.taverna.databundle;

import java.nio.file.Files;
import java.nio.file.Path;

public class DataBundle implements AutoCloseable {

	private final Path root;

	public DataBundle(Path root) {
		this.root = root;
	}

	public Path getRoot() {
		return root;
	}

	@Override
	public void close() throws Exception {
		Path orig = DataBundles.closeDataBundle(this);
		Files.deleteIfExists(orig);
	}

}
