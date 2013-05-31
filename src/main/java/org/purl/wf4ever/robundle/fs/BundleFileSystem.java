package org.purl.wf4ever.robundle.fs;

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
    private final Path source;
    private final String separator;

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
        // De-reference the original ZIP file system so it can be
        // garbage collected
        origFS = null;
    }

    public URI getBaseURI() {
        return baseURI;
    }

    protected BundleFileStore getFileStore() {
        // We assume there's only one file store, as is true for ZipProvider
        return new BundleFileStore(this, getOrigFS().getFileStores().iterator()
                .next());
    }

    /**
     * Thread-safe ClosedFileSystemException test
     * 
     * @return
     */
    protected FileSystem getOrigFS() {
        FileSystem orig = origFS;
        if (orig == null || !orig.isOpen()) {
            throw new ClosedFileSystemException();
        }
        return orig;
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return Collections.<FileStore> singleton(getFileStore());
    }

    @Override
    public Path getPath(String first, String... more) {
        Path zipPath = getOrigFS().getPath(first, more);
        return wrap(zipPath);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        final PathMatcher zipMatcher = getOrigFS().getPathMatcher(
                syntaxAndPattern);
        return new PathMatcher() {
            @Override
            public boolean matches(Path path) {
                return zipMatcher.matches(unwrap(path));
            }
        };
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.<Path> singleton(getRootDirectory());
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

    protected Path findSource() {
        Path zipRoot = getRootDirectory().getZipPath();
        URI uri = zipRoot.toUri();
        String s = uri.getSchemeSpecificPart();
        if (!s.endsWith("!/")) { // sanity check
            throw new IllegalStateException("Can't parse JAR URI: " + uri);
        }
        URI zip = URI.create(s.substring(0, s.length() - 2));
        return Paths.get(zip); // Look up our path
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        if (origFS == null) {
            return false;
        }
        return origFS.isOpen();
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

}
