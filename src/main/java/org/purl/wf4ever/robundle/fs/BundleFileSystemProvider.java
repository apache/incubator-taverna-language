package org.purl.wf4ever.robundle.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
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

public class BundleFileSystemProvider extends FileSystemProvider {
    private static final String APPLICATION_VND_WF4EVER_ROBUNDLE_ZIP = "application/vnd.wf4ever.robundle+zip";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String WIDGET = "widget";

    /**
     * Public constructor provided for FileSystemProvider.installedProviders().
     * Use #getInstance() instead.
     * 
     * @deprecated
     */
    @Deprecated
    public BundleFileSystemProvider() {
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    protected static void addMimeTypeToZip(ZipOutputStream out, String mimetype)
            throws IOException {
        if (mimetype == null) {
            mimetype = APPLICATION_VND_WF4EVER_ROBUNDLE_ZIP;
        }
        // FIXME: Make the mediatype a parameter
        byte[] bytes = mimetype.getBytes(UTF8);

        // We'll have to do the mimetype file quite low-level
        // in order to ensure it is STORED and not COMPRESSED

        ZipEntry entry = new ZipEntry("mimetype");
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

    private static class Singleton {
        // Fallback for OSGi environments
        private static final BundleFileSystemProvider INSTANCE = new BundleFileSystemProvider();
    }

    public static BundleFileSystemProvider getInstance() {
        for (FileSystemProvider provider : FileSystemProvider
                .installedProviders()) {
            if (provider instanceof BundleFileSystemProvider) {
                return (BundleFileSystemProvider) provider;
            }
        }
        // Fallback for OSGi environments
        return Singleton.INSTANCE;
    }

    public static BundleFileSystem newFileSystemFromExisting(Path bundle)
            throws FileNotFoundException, IOException {
        URI w;
        try {
            w = new URI("widget", bundle.toUri().toASCIIString(), null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Can't create widget: URI for "
                    + bundle);
        }

        Map<String, Object> options = new HashMap<>();

        // useTempFile not needed as we override
        // newByteChannel to use newFileChannel() - which don't
        // consume memory
        //        options.put("useTempFile", true);

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
        Path tempDir = Files.createTempDirectory("robundle");
        // Why inside a tempDir? Because ZipFileSystemProvider
        // creates neighbouring temporary files
        // per file that is written to zip, which could mean a lot of
        // temporary files directly in /tmp - making it difficult to clean up
        Path bundle = tempDir.resolve("robundle.zip");
        BundleFileSystem fs = BundleFileSystemProvider.newFileSystemFromNew(
                bundle, null);
        return fs;
    }

    /**
     * The list of open file systems. This is static so that it is shared across
     * eventual multiple instances of this provider (such as when running in an
     * OSGi environment). Access to this map should be synchronized to avoid
     * opening a file system that is not in the map.
     */
    protected static Map<URI, WeakReference<BundleFileSystem>> openFilesystems = new HashMap<>();

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
    public <V extends FileAttributeView> V getFileAttributeView(Path path,
            Class<V> type, LinkOption... options) {
        BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
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
        return WIDGET;
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
    public InputStream newInputStream(Path path, OpenOption... options)
            throws IOException {
        // Avoid copying out to a file, like newByteChannel / newFileChannel
        BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
        return origProvider(path).newInputStream(fs.unwrap(path), options);
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
                    && ! Files.exists(zipPath)) {
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

                EnumSet<StandardOpenOption> opts = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
                origProvider(path).newFileChannel(zipPath, opts, attrs)
                        .close();

            }
        }

        // Implement by newFileChannel to avoid memory leaks and 
        // allow manifest to be updated
        return newFileChannel(path, options, attrs);
    }

    @Override
    public FileChannel newFileChannel(Path path,
            Set<? extends OpenOption> options, FileAttribute<?>... attrs)
            throws IOException {
        final BundleFileSystem fs = (BundleFileSystem) path.getFileSystem();
        return origProvider(path).newFileChannel(fs.unwrap(path), options,
                attrs);
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
    public BundleFileSystem newFileSystem(URI uri, Map<String, ?> env)
            throws IOException {

        Path localPath = localPathFor(uri);
        URI baseURI = baseURIFor(uri);

        // Open using ZIP provider
        URI jar;
        try {
            jar = new URI("jar", localPath.toUri().toString(), null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Can't construct JAR uri for "
                    + localPath);
        }
        // Pass on env
        FileSystem origFs = FileSystems.newFileSystem(jar, env);

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
            fs = new BundleFileSystem(origFs, baseURI);
            openFilesystems.put(baseURI,
                    new WeakReference<BundleFileSystem>(fs));
        }
        return fs;
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
