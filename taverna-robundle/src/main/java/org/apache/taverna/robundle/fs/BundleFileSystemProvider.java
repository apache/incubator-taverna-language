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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.taverna.robundle.utils.TemporaryFiles;

public class BundleFileSystemProvider extends FileSystemProvider {
	public class BundleFileChannel extends FileChannel {

		@SuppressWarnings("unused")
		private FileAttribute<?>[] attrs;
		private FileChannel fc;
		@SuppressWarnings("unused")
		private Set<? extends OpenOption> options;
		@SuppressWarnings("unused")
		private Path path;

		public BundleFileChannel(FileChannel fc, Path path,
				Set<? extends OpenOption> options, FileAttribute<?>[] attrs) {
			this.fc = fc;
			this.path = path;
			this.options = options;
			this.attrs = attrs;
		}

		@Override
		public void force(boolean metaData) throws IOException {
			fc.force(metaData);
		}

		@Override
		protected void implCloseChannel() throws IOException {
			fc.close();
			// TODO: Update manifest
		}

		@Override
		public FileLock lock(long position, long size, boolean shared)
				throws IOException {
			return fc.lock(position, size, shared);
		}

		@Override
		public MappedByteBuffer map(MapMode mode, long position, long size)
				throws IOException {
			return fc.map(mode, position, size);
		}

		@Override
		public long position() throws IOException {
			return fc.position();
		}

		@Override
		public FileChannel position(long newPosition) throws IOException {
			return fc.position(newPosition);
		}

		@Override
		public int read(ByteBuffer dst) throws IOException {
			return fc.read(dst);
		}

		@Override
		public int read(ByteBuffer dst, long position) throws IOException {
			return fc.read(dst, position);
		}

		@Override
		public long read(ByteBuffer[] dsts, int offset, int length)
				throws IOException {
			return fc.read(dsts, offset, length);
		}

		@Override
		public long size() throws IOException {
			return fc.size();
		}

		@Override
		public long transferFrom(ReadableByteChannel src, long position,
				long count) throws IOException {
			return fc.transferFrom(src, position, count);
		}

		@Override
		public long transferTo(long position, long count,
				WritableByteChannel target) throws IOException {
			return fc.transferTo(position, count, target);
		}

		@Override
		public FileChannel truncate(long size) throws IOException {
			return fc.truncate(size);
		}

		@Override
		public FileLock tryLock(long position, long size, boolean shared)
				throws IOException {
			return fc.tryLock(position, size, shared);
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			return fc.write(src);
		}

		@Override
		public int write(ByteBuffer src, long position) throws IOException {
			return fc.write(src, position);
		}

		@Override
		public long write(ByteBuffer[] srcs, int offset, int length)
				throws IOException {
			return fc.write(srcs, offset, length);
		}

	}

	private static class Singleton {
		// Fallback for OSGi environments
		private static final BundleFileSystemProvider INSTANCE = new BundleFileSystemProvider();
	}

	private static final String APP = "app";

	public static final String APPLICATION_VND_WF4EVER_ROBUNDLE_ZIP = "application/vnd.wf4ever.robundle+zip";
	public static final String MIMETYPE_FILE = "mimetype";

	/**
	 * The list of open file systems. This is static so that it is shared across
	 * eventual multiple instances of this provider (such as when running in an
	 * OSGi environment). Access to this map should be synchronized to avoid
	 * opening a file system that is not in the map.
	 */
	protected static Map<URI, WeakReference<BundleFileSystem>> openFilesystems = new HashMap<>();

	private static final Charset UTF8 = Charset.forName("UTF-8");

	protected static void addMimeTypeToZip(ZipOutputStream out, String mimetype)
			throws IOException {
		if (mimetype == null) {
			mimetype = APPLICATION_VND_WF4EVER_ROBUNDLE_ZIP;
		}
		// FIXME: Make the mediatype a parameter
		byte[] bytes = mimetype.getBytes(UTF8);

		// We'll have to do the mimetype file quite low-level
		// in order to ensure it is STORED and not COMPRESSED

		ZipEntry entry = new ZipEntry(MIMETYPE_FILE);
		entry.setMethod(ZipEntry.STORED);
		entry.setSize(bytes.length);
		CRC32 crc = new CRC32();
		crc.update(bytes);
		entry.setCrc(crc.getValue());

		out.putNextEntry(entry);
		out.write(bytes);
		out.closeEntry();
	}

	protected static void createBundleAsZip(Path bundle, String mimetype)
			throws FileNotFoundException, IOException {
		// Create ZIP file as
		// http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(
				bundle, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING))) {
			addMimeTypeToZip(out, mimetype);
		}
	}

	public static BundleFileSystemProvider getInstance() {
		for (FileSystemProvider provider : FileSystemProvider
				.installedProviders()) {
			if (provider instanceof BundleFileSystemProvider) {
				return (BundleFileSystemProvider) provider;
			}
		}
		// Not installed!
		// Fallback for OSGi environments
		return Singleton.INSTANCE;
	}

	public static BundleFileSystem newFileSystemFromExisting(Path bundle)
			throws FileNotFoundException, IOException {
		URI w;
		try {
			w = new URI(APP, bundle.toUri().toASCIIString(), null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Can't create app: URI for "
					+ bundle);
		}

		Map<String, Object> options = new HashMap<>();

		// useTempFile not needed as we override
		// newByteChannel to use newFileChannel() - which don't
		// consume memory
		// options.put("useTempFile", true);

		FileSystem fs = FileSystems.newFileSystem(w, options,
				BundleFileSystemProvider.class.getClassLoader());
		return (BundleFileSystem) fs;

		// To avoid multiple instances of this provider in an OSGi environment,
		// the above official API calls could be replaced with:

		// return getInstance().newFileSystem(w, Collections.<String, Object>
		// emptyMap());

		// which would fall back to Singleton.INSTANCE if there is no provider.
	}

	public static BundleFileSystem newFileSystemFromNew(Path bundle)
			throws FileNotFoundException, IOException {
		return newFileSystemFromNew(bundle,
				APPLICATION_VND_WF4EVER_ROBUNDLE_ZIP);
	}

	public static BundleFileSystem newFileSystemFromNew(Path bundle,
			String mimetype) throws FileNotFoundException, IOException {
		createBundleAsZip(bundle, mimetype);
		return newFileSystemFromExisting(bundle);
	}

	public static BundleFileSystem newFileSystemFromTemporary()
			throws IOException {
		Path bundle = TemporaryFiles.temporaryBundle();
		BundleFileSystem fs = BundleFileSystemProvider.newFileSystemFromNew(
				bundle, null);
		return fs;
	}

	private Boolean jarDoubleEscaping;

	/**
	 * Public constructor provided for FileSystemProvider.installedProviders().
	 * Use #getInstance() instead.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public BundleFileSystemProvider() {
	}

	private boolean asBoolean(Object object, boolean defaultValue) {
		if (object instanceof Boolean) {
			return (Boolean) object;
		}
		if (object instanceof String) {
			return Boolean.valueOf((String) object);
		}
		return defaultValue;
	}

	protected URI baseURIFor(URI uri) {
		if (!(uri.getScheme().equals(APP))) {
			throw new IllegalArgumentException("Unsupported scheme in: " + uri);
		}
		if (!uri.isOpaque()) {
			return uri.resolve("/");
		}
		Path localPath = localPathFor(uri);
		Path realPath;
		try {
			realPath = localPath.toRealPath();
		} catch (IOException ex) {
			realPath = localPath.toAbsolutePath();
		}
		// Generate a UUID from the MD5 of the URI of the real path (!)
		UUID uuid = UUID.nameUUIDFromBytes(realPath.toUri().toASCIIString()
				.getBytes(UTF8));
		try {
			return new URI(APP, uuid.toString(), "/", null);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't create app:// URI for: "
					+ uuid);
		}
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		origProvider(path).checkAccess(fs.unwrap(path), modes);
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options)
			throws IOException {
		BundleFileSystem fs = (BundleFileSystem) source.getFileSystem();
		origProvider(source)
				.copy(fs.unwrap(source), fs.unwrap(target), options);
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs)
			throws IOException {
		// Workaround http://stackoverflow.com/questions/16588321/
		if (Files.exists(dir)) {
			throw new FileAlreadyExistsException(dir.toString());
		}
		BundleFileSystem fs = (BundleFileSystem) dir.getFileSystem();
		origProvider(dir).createDirectory(fs.unwrap(dir), attrs);
	}

	@Override
	public void delete(Path path) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		origProvider(path).delete(fs.unwrap(path));
	}

	@Override
	public boolean equals(Object obj) {
		return getClass() == obj.getClass();
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path,
			Class<V> type, LinkOption... options) {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		if (path.toAbsolutePath().equals(fs.getRootDirectory())) {
			// Bug in ZipFS, it will fall over as there is no entry for /
			//
			// Instead we'll just give a view of the source (e.g. the zipfile
			// itself).
			// Modifying its times is a bit futile since they are likely to be
			// overriden when closing, but this avoids a NullPointerException
			// in Files.setTimes().
			return Files.getFileAttributeView(fs.getSource(), type, options);
		}
		return origProvider(path).getFileAttributeView(fs.unwrap(path), type,
				options);
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		BundlePath bpath = (BundlePath) path;
		return bpath.getFileSystem().getFileStore();
	}

	@Override
	public BundleFileSystem getFileSystem(URI uri) {
		synchronized (openFilesystems) {
			URI baseURI = baseURIFor(uri);
			WeakReference<BundleFileSystem> ref = openFilesystems.get(baseURI);
			if (ref == null) {
				throw new FileSystemNotFoundException(uri.toString());
			}
			BundleFileSystem fs = ref.get();
			if (fs == null) {
				openFilesystems.remove(baseURI);
				throw new FileSystemNotFoundException(uri.toString());
			}
			return fs;
		}
	}

	protected boolean getJarDoubleEscaping() {
		if (jarDoubleEscaping != null) {
			return jarDoubleEscaping;
		}
		// https://bugs.openjdk.java.net/browse/JDK-8001178 introduced an
		// inconsistent
		// URI syntax. Before 7u40, jar: URIs to ZipFileSystemProvided had to
		// have
		// double-escaped the URI for the ZIP file, after 7u40 it is only
		// escaped once.
		// E.g.
		// to open before 7u40 you needed
		// jar:file:///file%2520with%2520spaces.zip, now you need
		// jar:file:///file%20with%20spaces.zip
		//
		// The new format is now consistent with URL.openStream() and
		// URLClassLoader's traditional jar: syntax, but somehow
		// zippath.toUri() still returns the double-escaped one, which
		// should only affects BundleFileSystem.findSource(). To help
		// findSource()
		// if this new bug is later fixed, we here detect which escaping style
		// is used.

		String name = "jar test";
		try {
			Path tmp = Files.createTempFile(name, ".zip");
			if (!tmp.toUri().toASCIIString().contains("jar%20test")) {
				// Hmm.. spaces not allowed in tmp? As we don't know, we'll
				// assume Java 7 behaviour
				jarDoubleEscaping = false;
				return jarDoubleEscaping;
			}
			createBundleAsZip(tmp, null);
			try (FileSystem fs = FileSystems.newFileSystem(tmp, null)) {
				URI root = fs.getRootDirectories().iterator().next().toUri();
				if (root.toASCIIString().contains("jar%2520test")) {
					jarDoubleEscaping = true;
				} else {
					jarDoubleEscaping = false;
				}
			}
			Files.delete(tmp);
		} catch (IOException e) {
			// Unknown error.. we'll assume Java 7 behaviour
			jarDoubleEscaping = true;
		}
		return jarDoubleEscaping;

	}

	@Override
	public Path getPath(URI uri) {
		BundleFileSystem fs = getFileSystem(uri);
		Path r = fs.getRootDirectory();
		if (uri.isOpaque()) {
			return r;
		} else {
			return r.resolve(uri.getPath());
		}
	}

	@Override
	public String getScheme() {
		return APP;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).isHidden(fs.unwrap(path));
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).isSameFile(fs.unwrap(path), fs.unwrap(path2));
	}

	private Path localPathFor(URI uri) {
		URI localUri = URI.create(uri.getSchemeSpecificPart());
		return Paths.get(localUri);
	}

	@Override
	public void move(Path source, Path target, CopyOption... options)
			throws IOException {
		BundleFileSystem fs = (BundleFileSystem) source.getFileSystem();
		origProvider(source)
				.copy(fs.unwrap(source), fs.unwrap(target), options);
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path,
			Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		final BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		Path zipPath = fs.unwrap(path);
		if (options.contains(StandardOpenOption.WRITE)
				|| options.contains(StandardOpenOption.APPEND)) {

			if (Files.isDirectory(zipPath)) {
				// Workaround for ZIPFS allowing dir and folder to somewhat
				// co-exist
				throw new FileAlreadyExistsException("Directory <"
						+ zipPath.toString() + "> exists");
			}
			Path parent = zipPath.getParent();

			if (parent != null && !Files.isDirectory(parent)) {
				throw new NoSuchFileException(zipPath.toString(),
						parent.toString(), "Parent of file is not a directory");
			}
			if (options.contains(StandardOpenOption.CREATE_NEW)) {
			} else if (options.contains(StandardOpenOption.CREATE)
					&& !Files.exists(zipPath)) {
				// Workaround for bug in ZIPFS in Java 7 -
				// it only creates new files on
				// StandardOpenOption.CREATE_NEW
				//
				// We'll fake it and just create file first using the legacy
				// newByteChannel()
				// - we can't inject CREATE_NEW option as it
				// could be that there are two concurrent calls to CREATE
				// the very same file,
				// with CREATE_NEW the second thread would then fail.

				EnumSet<StandardOpenOption> opts = EnumSet
						.of(StandardOpenOption.WRITE,
								StandardOpenOption.CREATE_NEW);
				origProvider(path).newFileChannel(zipPath, opts, attrs).close();

			}
		}

		// Implement by newFileChannel to avoid memory leaks and
		// allow manifest to be updated
		return newFileChannel(path, options, attrs);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir,
			final Filter<? super Path> filter) throws IOException {
		final BundleFileSystem fs = (BundleFileSystem) dir.getFileSystem();
		final DirectoryStream<Path> stream = origProvider(dir)
				.newDirectoryStream(fs.unwrap(dir), new Filter<Path>() {
					@Override
					public boolean accept(Path entry) throws IOException {
						return filter.accept(fs.wrap(entry));
					}
				});
		return new DirectoryStream<Path>() {
			@Override
			public void close() throws IOException {
				stream.close();
			}

			@Override
			public Iterator<Path> iterator() {
				return fs.wrapIterator(stream.iterator());
			}
		};
	}

	@Override
	public FileChannel newFileChannel(Path path,
			Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		final BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		FileChannel fc = origProvider(path).newFileChannel(fs.unwrap(path),
				options, attrs);
		return new BundleFileChannel(fc, path, options, attrs);
	}

	@Override
	public FileSystem newFileSystem(Path path, Map<String, ?> env)
			throws IOException {
		URI uri;
		try {
			uri = new URI(APP, path.toUri().toASCIIString(), null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Can't create app: URI for "
					+ path);
		}
		return newFileSystem(uri, env);
	}

	@Override
	public BundleFileSystem newFileSystem(URI uri, Map<String, ?> env)
			throws IOException {

		Path localPath = localPathFor(uri);
		URI baseURI = baseURIFor(uri);

		if (asBoolean(env.get("create"), false)) {
			createBundleAsZip(localPath, (String) env.get("mimetype"));
		}

		BundleFileSystem fs;
		synchronized (openFilesystems) {
			WeakReference<BundleFileSystem> existingRef = openFilesystems
					.get(baseURI);
			if (existingRef != null) {
				BundleFileSystem existing = existingRef.get();
				if (existing != null && existing.isOpen()) {
					throw new FileSystemAlreadyExistsException(
							baseURI.toASCIIString());
				}
			}
			FileSystem origFs = FileSystems.newFileSystem(localPath, null);
			fs = new BundleFileSystem(origFs, baseURI);
			openFilesystems.put(baseURI,
					new WeakReference<BundleFileSystem>(fs));
		}
		return fs;
	}

	@Override
	public InputStream newInputStream(Path path, OpenOption... options)
			throws IOException {
		// Avoid copying out to a file, like newByteChannel / newFileChannel
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).newInputStream(fs.unwrap(path), options);
	}

	@Override
	public OutputStream newOutputStream(Path path, OpenOption... options)
			throws IOException {
		BundleFileSystem fileSystem = (BundleFileSystem) path.getFileSystem();
		if (fileSystem.getRootDirectory().resolve(path)
				.equals(fileSystem.getRootDirectory().resolve(MIMETYPE_FILE))) {
			// Special case to avoid compression
			return origProvider(path).newOutputStream(fileSystem.unwrap(path),
					options);
		}
		return super.newOutputStream(path, options);
	}

	private FileSystemProvider origProvider(Path path) {
		return ((BundlePath) path).getFileSystem().getOrigFS().provider();
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path,
			Class<A> type, LinkOption... options) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path)
				.readAttributes(fs.unwrap(path), type, options);
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes,
			LinkOption... options) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).readAttributes(fs.unwrap(path), attributes,
				options);
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value,
			LinkOption... options) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		origProvider(path).setAttribute(fs.unwrap(path), attribute, value,
				options);
	}

}
