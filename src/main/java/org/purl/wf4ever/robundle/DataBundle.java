package org.purl.wf4ever.robundle;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataBundle implements AutoCloseable {

	private boolean deleteOnClose;
	private final Path root;

	public DataBundle(Path root, boolean deleteOnClose) {
		this.root = root;
		this.setDeleteOnClose(deleteOnClose);
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

	public boolean isDeleteOnClose() {
		return deleteOnClose;
	}

	public void setDeleteOnClose(boolean deleteOnClose) {
		this.deleteOnClose = deleteOnClose;
	}

}
