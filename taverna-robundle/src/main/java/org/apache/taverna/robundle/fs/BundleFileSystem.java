package org.apache.taverna.robundle.fs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.net.URI;
import java.nio.file.ClosedFileSystemException;
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

	protected final URI baseURI;
	private FileSystem origFS;
	private final String separator;
	private final Path source;

	protected BundleFileSystem(FileSystem origFS, URI baseURI) {
		if (origFS == null || baseURI == null) {
			throw new NullPointerException();
		}
		this.origFS = origFS;
		this.baseURI = baseURI;
		this.separator = origFS.getSeparator();
		this.source = findSource();
	}

	@Override
	public void close() throws IOException {
		if (origFS == null) {
			return;
		}
		origFS.close();
		origFS = null;
	}

	protected Path findSource() {
		URI uri = this.baseURI;

		if (uri.getScheme().equals("arcp")) {
			// Handle arcp:// URI scheme separately
			return Paths.get(uri.getPath());
		}

		// Default handling for zip URIs
		Path zipRoot = getRootDirectory().getZipPath();
		String schemeSpecific = uri.getRawSchemeSpecificPart();

		if (!schemeSpecific.endsWith("!/")) {
			throw new IllegalStateException("Can't parse URI: " + uri);
		}

		URI zip = URI.create(schemeSpecific.substring(0, schemeSpecific.length() - 2));
		return Paths.get(zip); // Look up our path
	}

	public URI getBaseURI() {
		return baseURI;
	}

	protected BundleFileStore getFileStore() {
		return new BundleFileStore(this, getOrigFS().getFileStores().iterator().next());
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return Collections.singleton(getFileStore());
	}

	protected FileSystem getOrigFS() {
		FileSystem orig = origFS;
		if (orig == null || !orig.isOpen()) {
			throw new ClosedFileSystemException();
		}
		return orig;
	}

	@Override
	public Path getPath(String first, String... more) {
		Path zipPath = getOrigFS().getPath(first, more);
		return wrap(zipPath);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		final PathMatcher zipMatcher = getOrigFS().getPathMatcher(syntaxAndPattern);
		return new PathMatcher() {
			@Override
			public boolean matches(Path path) {
				return zipMatcher.matches(unwrap(path));
			}
		};
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		return Collections.singleton(getRootDirectory());
	}

	public BundlePath getRootDirectory() {
		return wrap(getOrigFS().getRootDirectories().iterator().next());
	}

	@Override
	public String getSeparator() {
		return separator;
	}

	public Path getSource() {
		return source;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		return origFS != null && origFS.isOpen();
	}

	@Override
	public boolean isReadOnly() {
		return getOrigFS().isReadOnly();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BundleFileSystemProvider provider() {
		return BundleFileSystemProvider.getInstance();
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		if (origFS == null) {
			throw new ClosedFileSystemException();
		}
		return origFS.supportedFileAttributeViews();
	}

	protected Path unwrap(Path bundlePath) {
		if (!(bundlePath instanceof BundlePath)) {
			return bundlePath;
		}
		return ((BundlePath) bundlePath).getZipPath();
	}

	protected static Path withoutSlash(Path dir) {
		if (dir == null) {
			return null;
		}
		Path fname = dir.getFileName();
		if (fname == null) // Root directory?
			return dir;
		String fnameStr = fname.toString();
		if (!fnameStr.endsWith("/") && !fnameStr.equals("/"))
			return dir;
		return dir.resolveSibling(fnameStr.replace("/", ""));
	}

	protected BundlePath wrap(Path zipPath) {
		if (zipPath == null) {
			return null;
		}
		if (zipPath instanceof BundlePath) {
			throw new IllegalArgumentException("Did not expect BundlePath: " + zipPath);
		}

		return new BundlePath(this, withoutSlash(zipPath));
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
}
