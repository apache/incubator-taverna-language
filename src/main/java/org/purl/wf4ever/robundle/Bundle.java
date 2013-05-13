package org.purl.wf4ever.robundle;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.purl.wf4ever.robundle.fs.BundleFileSystem;
import org.purl.wf4ever.robundle.fs.BundlePath;

public class Bundle implements AutoCloseable {

	private boolean deleteOnClose;
	private final Path root;

	public Bundle(Path root, boolean deleteOnClose) {
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
		BundleFileSystem fs = (BundleFileSystem) getRoot().getFileSystem();
		return fs.getSource();
		

	}

	public boolean isDeleteOnClose() {
		return deleteOnClose;
	}

	public void setDeleteOnClose(boolean deleteOnClose) {
		this.deleteOnClose = deleteOnClose;
	}

}
