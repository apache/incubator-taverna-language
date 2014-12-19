package org.purl.wf4ever.robundle.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class BundleFileStore extends FileStore {

	// private final BundleFileSystem fs;
	private final FileStore origFileStore;

	protected BundleFileStore(BundleFileSystem fs, FileStore origFileStore) {
		if (fs == null || origFileStore == null) {
			throw new NullPointerException();
		}
		// this.fs = fs;
		this.origFileStore = origFileStore;
	}

	public Object getAttribute(String attribute) throws IOException {
		return origFileStore.getAttribute(attribute);
	}

	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(
			Class<V> type) {
		return origFileStore.getFileStoreAttributeView(type);
	}

	public long getTotalSpace() throws IOException {
		return origFileStore.getTotalSpace();
	}

	public long getUnallocatedSpace() throws IOException {
		return origFileStore.getUnallocatedSpace();
	}

	public long getUsableSpace() throws IOException {
		return origFileStore.getUsableSpace();
	}

	public boolean isReadOnly() {
		return origFileStore.isReadOnly();
	}

	public String name() {
		return origFileStore.name();
	}

	public boolean supportsFileAttributeView(
			Class<? extends FileAttributeView> type) {
		return origFileStore.supportsFileAttributeView(type);
	}

	public boolean supportsFileAttributeView(String name) {
		return origFileStore.supportsFileAttributeView(name);
	}

	public String toString() {
		return origFileStore.toString();
	}

	public String type() {
		return "bundle";
	}

}
