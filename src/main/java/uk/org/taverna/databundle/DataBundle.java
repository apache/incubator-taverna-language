package uk.org.taverna.databundle;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataBundle implements AutoCloseable {

	private final Path root;
	private boolean deleteOnClose;

	public DataBundle(Path root, boolean deleteOnClose) {
		this.root = root;
		this.setDeleteOnClose(deleteOnClose);
	}

	public Path getRoot() {
		return root;
	}

	public Path getSource() {
		URI uri = getRoot().toUri();
		String s = uri.getSchemeSpecificPart();
		if (!s.endsWith("!/")) { // sanity check
			throw new IllegalStateException("Can't parse JAR URI: " + uri);
		}
		URI zip = URI.create(s.substring(0, s.length() - 2));
		return Paths.get(zip); // Look up our path
	}

	@Override
	public void close() throws IOException {
		close(isDeleteOnClose());
	}

	protected void close(boolean deleteOnClose) throws IOException {
		getRoot().getFileSystem().close();
		if (deleteOnClose) {
			Files.deleteIfExists(getSource());
		}
	}

	public boolean isDeleteOnClose() {
		return deleteOnClose;
	}

	public void setDeleteOnClose(boolean deleteOnClose) {
		this.deleteOnClose = deleteOnClose;
	}

}
