package org.purl.wf4ever.robundle.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

public class BundleFileSystem extends FileSystem {

	private FileSystem origFS;
	private BundleFileSystemProvider provider;
	private URI baseURI;

	protected BundleFileSystem(FileSystem origFS, BundleFileSystemProvider provider, URI baseURI) {
		this.origFS = origFS;
		this.provider = provider;
		this.baseURI = baseURI;
	}

	@Override
	public FileSystemProvider provider() {
		return provider;
	}

	@Override
	public void close() throws IOException {
		origFS.close();		
	}

	@Override
	public boolean isOpen() {
		return origFS.isOpen();
	}

	@Override
	public boolean isReadOnly() {
		return origFS.isReadOnly();
	}

	@Override
	public String getSeparator() {
		return origFS.getSeparator();
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path getPath(String first, String... more) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}
	
}
