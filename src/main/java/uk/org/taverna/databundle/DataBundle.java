package uk.org.taverna.databundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataBundle implements AutoCloseable {

	private final Path root;
	private boolean deleteOnClose;
	public DataBundle(Path root, boolean deleteOnClose) {
		this.root = root;
		this.deleteOnClose = deleteOnClose;
	}

	public Path getRoot() {
		return root;
	}

	@Override
	public void close() throws IOException  {
		if (getRoot().getFileSystem().isOpen()) {
			Path orig = DataBundles.closeDataBundle(this);
			if (deleteOnClose) {
				Files.deleteIfExists(orig);
			}
		}
	}

}
