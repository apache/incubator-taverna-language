package org.purl.wf4ever.robundle.fs;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BundleFileSystemProvider extends FileSystemProvider {

	Map<URI, WeakReference<BundleFileSystem>> openFilesystems = new HashMap<>();

	private static final String WIDGET = "widget";

	private static final Charset UTF8 = Charset.forName("UTF8");

	@Override
	public String getScheme() {
		return WIDGET;
	}

	@Override
	public BundleFileSystem newFileSystem(URI uri, Map<String, ?> env)
			throws IOException {

		Path localPath = localPathFor(uri);
		URI baseURI = baseURIFor(uri);

		FileSystem origFs = FileSystems.newFileSystem(localPath, null);

		BundleFileSystem fs;
		synchronized (openFilesystems) {
			WeakReference<BundleFileSystem> existingRef = openFilesystems
					.get(baseURI);
			if (existingRef != null) {
				BundleFileSystem existing = existingRef.get();
				if (existing.isOpen()) {
					throw new FileSystemAlreadyExistsException(
							baseURI.toASCIIString());
				}
			}
			fs = new BundleFileSystem(origFs, this, baseURI);
			openFilesystems.put(baseURI,
					new WeakReference<BundleFileSystem>(fs));
		}
		return fs;
	}

	private Path localPathFor(URI uri) {
		URI localUri = URI.create(uri.getSchemeSpecificPart());
		return Paths.get(localUri);
	}

	protected URI baseURIFor(URI uri) {
		if (!(uri.getScheme().equals(WIDGET))) {
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
			return new URI(WIDGET, uuid.toString(), "/", null);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't create widget:// URI for: "
					+ uuid);
		}
	}

	@Override
	public BundleFileSystem getFileSystem(URI uri) {
		WeakReference<BundleFileSystem> ref = openFilesystems
				.get(baseURIFor(uri));
		if (ref == null) {
			throw new FileSystemNotFoundException(uri.toString());
		}
		BundleFileSystem fs = ref.get();
		if (fs == null) {
			throw new FileSystemNotFoundException(uri.toString());
		}
		return fs;
	}

	@Override
	public Path getPath(URI uri) {
		BundleFileSystem fs = getFileSystem(uri);
		Path r = fs.getRoot();
		if (uri.isOpaque()) {
			return r;
		} else {
			return r.resolve(uri.getPath());
		}
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path,
			Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {		
		final BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).newByteChannel(fs.unwrap(path), options, attrs);
	}

	private FileSystemProvider origProvider(Path path) {
		return ((BundlePath) path).getFileSystem().origFS.provider();
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
	public void createDirectory(Path dir, FileAttribute<?>... attrs)
			throws IOException {
		BundleFileSystem fs = (BundleFileSystem) dir.getFileSystem();
		origProvider(dir).createDirectory(fs.unwrap(dir), attrs);
	}

	@Override
	public void delete(Path path) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		origProvider(path).delete(fs.unwrap(path));
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options)
			throws IOException {
		BundleFileSystem fs = (BundleFileSystem) source.getFileSystem();
		origProvider(source).copy(fs.unwrap(source), fs.unwrap(target), options);
	}

	@Override
	public void move(Path source, Path target, CopyOption... options)
			throws IOException {
		BundleFileSystem fs = (BundleFileSystem) source.getFileSystem();
		origProvider(source).copy(fs.unwrap(source), fs.unwrap(target), options);
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).isSameFile(fs.unwrap(path), fs.unwrap(path2));
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).isHidden(fs.unwrap(path));
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		BundlePath bpath = (BundlePath)path;
		return bpath.getFileSystem().getFileStore();
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		origProvider(path).checkAccess(fs.unwrap(path), modes);
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path,
			Class<V> type, LinkOption... options) {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).getFileAttributeView(fs.unwrap(path), type, options);
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path,
			Class<A> type, LinkOption... options) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).readAttributes(fs.unwrap(path), type, options);
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes,
			LinkOption... options) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		return origProvider(path).readAttributes(fs.unwrap(path), attributes, options);
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value,
			LinkOption... options) throws IOException {
		BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
		origProvider(path).setAttribute(fs.unwrap(path), attribute, value, options);
	}

}
