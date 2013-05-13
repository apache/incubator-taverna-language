package org.purl.wf4ever.robundle.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class BundleFileSystem extends FileSystem {

	protected final FileSystem origFS;
	protected final BundleFileSystemProvider provider;
	protected final URI baseURI;

	protected BundleFileSystem(FileSystem origFS,
			BundleFileSystemProvider provider, URI baseURI) {
		if (origFS == null || provider == null || baseURI == null) {
			throw new NullPointerException();
		}
		this.origFS = origFS;
		this.provider = provider;
		this.baseURI = baseURI;
	}

	protected Path unwrap(Path bundlePath) {
		if (!(bundlePath instanceof BundlePath)) {
			// assume it's already unwrapped for some reason (for instance being
			// null)
			return bundlePath;
		}
		return ((BundlePath) bundlePath).getZipPath();
	}

	protected BundlePath wrap(Path zipPath) {
		if (zipPath == null) {
			return null;
		}
		if (zipPath instanceof BundlePath) {
			throw new IllegalArgumentException("Did not expect BundlePath: "
					+ zipPath);
		}
		return new BundlePath(this, zipPath);
	}

	protected Iterator<Path> wrapIterator(final Iterator<Path> iterator) {
		return new Iterator<Path>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Path next() {
				return wrap(iterator.next());
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

	@Override
	public BundleFileSystemProvider provider() {
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
		return Collections.<Path> singleton(getRootDirectory());
	}

	public BundlePath getRootDirectory() {
		return wrap(origFS.getRootDirectories().iterator().next());
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return Collections.<FileStore> singleton(getFileStore());
	}

	protected BundleFileStore getFileStore() {
		// We assume there's only one file store, as is true for ZipProvider
		return new BundleFileStore(this, origFS.getFileStores().iterator()
				.next());
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		return origFS.supportedFileAttributeViews();
	}

	@Override
	public Path getPath(String first, String... more) {
		Path zipPath = origFS.getPath(first, more);
		return wrap(zipPath);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		final PathMatcher zipMatcher = origFS.getPathMatcher(syntaxAndPattern);
		return new PathMatcher() {
			@Override
			public boolean matches(Path path) {
				return zipMatcher.matches(unwrap(path));
			}
		};
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}

	public URI getBaseURI() {
		return baseURI;
	}

	public Path getSource() {
		Path zipRoot = getRootDirectory().getZipPath();
		URI uri = zipRoot.toUri();
		String s = uri.getSchemeSpecificPart();
		if (!s.endsWith("!/")) { // sanity check
			throw new IllegalStateException("Can't parse JAR URI: " + uri);
		}
		URI zip = URI.create(s.substring(0, s.length() - 2));
		return Paths.get(zip); // Look up our path
	}

}
