package org.purl.wf4ever.robundle;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.purl.wf4ever.robundle.fs.BundleFileSystem;
import org.purl.wf4ever.robundle.fs.BundleFileSystemProvider;

/**
 * Utility functions for dealing with RO bundles.
 * <p>
 * The style of using this class is similar to that of {@link Files}. In fact, a
 * RO bundle is implemented as a set of {@link Path}s.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class Bundles {

    protected static final class RecursiveCopyFileVisitor extends
            SimpleFileVisitor<Path> implements Closeable {
        private final CopyOption[] copyOptions;
        private final Set<CopyOption> copyOptionsSet;
        private final Path destination;
        private final LinkOption[] linkOptions;
        private final Path source;
        private boolean ignoreErrors;
        private ExecutorService workers;
        private Long concurrentTimeoutSeconds;
        protected IOException firstException;

        private RecursiveCopyFileVisitor(Path destination,
                Set<CopyOption> copyOptionsSet, Path source) {
            this.destination = destination;
            this.source = source;

            this.copyOptionsSet = new HashSet<CopyOption>(copyOptionsSet);
            
            HashSet<Object> linkOptionsSet = new HashSet<>();
            for (CopyOption option : copyOptionsSet) {
                copyOptionsSet.add(option);
                if (option instanceof LinkOption) {
                    linkOptionsSet.add((LinkOption) option);                    
                }
            }
            
            this.linkOptions = linkOptionsSet
                    .toArray(new LinkOption[(linkOptionsSet.size())]);
            
            this.ignoreErrors = copyOptionsSet.contains(RecursiveCopyOption.IGNORE_ERRORS);
            if (copyOptionsSet.contains(RecursiveCopyOption.THREADED)) {
                Integer maxWorkers = Integer.valueOf(System
                                .getProperty("bundles.copy.concurrent.max", Integer.toString(COPY_MAX_CONCURRENT)));
                concurrentTimeoutSeconds = Long.valueOf(System.getProperty(
                        "bundles.copy.concurrent.timeout_s",
                        Long.toString(Long.MAX_VALUE)));
                workers = Executors.newFixedThreadPool(maxWorkers);
            }
            
            // To avoid UnsupporteOperationException from java.nio operations
            // we strip our own options out
            
            copyOptionsSet.removeAll(EnumSet.allOf(RecursiveCopyOption.class));
            copyOptions = copyOptionsSet
                    .toArray(new CopyOption[(copyOptionsSet.size())]);
        }
        
        @Override
        public void close() throws IOException {
            if (workers != null) {
                workers.shutdown();
                try {
                    workers.awaitTermination(concurrentTimeoutSeconds, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new IOException("Timed out waiting for threaded recursive copy to complete", e);
                }
                if (firstException != null) {
                    throw firstException;
                }
            }
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
            try {
                if (copyOptionsSet.contains(StandardCopyOption.COPY_ATTRIBUTES)) {
                    // Copy file times
                    // Inspired by
                    // java.nio.file.CopyMoveHelper.copyToForeignTarget()
                    BasicFileAttributes attrs = Files.readAttributes(dir,
                            BasicFileAttributes.class, linkOptions);
                    BasicFileAttributeView view = Files.getFileAttributeView(
                            toDestination(dir), BasicFileAttributeView.class,
                            linkOptions);
                    view.setTimes(attrs.lastModifiedTime(),
                            attrs.lastAccessTime(), attrs.creationTime());
                }
                return FileVisitResult.CONTINUE;
            } catch (IOException ex) {
                return visitFileFailed(dir, ex);
            }
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) throws IOException {
            try {
                if (copyOptionsSet
                        .contains(StandardCopyOption.REPLACE_EXISTING)
                        && Files.isDirectory(dir)) {
                    return FileVisitResult.CONTINUE;
                }
                Files.copy(dir, toDestination(dir), copyOptions);
                // Files.createDirectory(toDestination(dir));
                return FileVisitResult.CONTINUE;
            } catch (IOException ex) {
                // Eat or rethrow depending on IGNORE_ERRORS
                return visitFileFailed(dir, ex);
            }
        }

        private Path toDestination(Path path) {
//            Path relativize = source.relativize(path);
//            return destination.resolve(relativize);
            // The above does not work as ZipPath throws ProviderMisMatchException
            // when given a relative filesystem Path
            
            URI rel = uriWithSlash(source).relativize(path.toUri());
            URI dest = uriWithSlash(destination).resolve(rel);            
            return Paths.get(dest);
        }
        

        private URI uriWithSlash(Path dir) {
            URI uri = dir.toUri();
            if (! uri.equals(uri.resolve("."))) {
                return uri.resolve(dir.getFileName().toString() +"/");
            }
            return uri;
        }

        @Override
        public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs)
                throws IOException {           

            if (workers == null) {
                return copyFile(file);
            }
            
            Runnable command = new Runnable() {
                @Override
                public void run() {
                    try {
                        copyFile(file);
                    } catch (IOException e) {
                       if (! ignoreErrors) {
                           exceptionInWorker(e);
                       }
                    }
                }
            };
            if (workers.isShutdown()) {
                return FileVisitResult.TERMINATE; 
            }
            workers.execute(command);
            return FileVisitResult.CONTINUE;
        
        }

        protected synchronized void exceptionInWorker(IOException e) {
            if (firstException != null) {
                return;
            }
            firstException = e;
            workers.shutdownNow();
        }

        private FileVisitResult copyFile(Path file) throws IOException {
            try {
                Files.copy(file, toDestination(file), copyOptions);
                return FileVisitResult.CONTINUE;
            } catch (IOException ex) {
                return visitFileFailed(file, ex);
            }
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
                throws IOException {
            if (ignoreErrors) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            // Or - throw exception
            return super.visitFileFailed(file, exc);
        }
    }

    public enum RecursiveCopyOption implements CopyOption {
        /**
         * Ignore any errors, copy as much as possible. The default is to stop
         * on the first IOException.
         * 
         */
        IGNORE_ERRORS,

        /**
         * Use concurrent copies, this can speed up copying of many small files.
         * The maximum number of threads in a particular recursive operation is
         * defined by the system property bundles.copy.concurrent.max, default
         * is 10.
         */
        THREADED,

    }

    protected static class RecursiveDeleteVisitor extends
            SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
            super.postVisitDirectory(dir, exc);
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }
    }

    protected static final String DOT_URL = ".url";

    private static final Charset ASCII = Charset.forName("ASCII");
    private static int COPY_MAX_CONCURRENT = 10;
    private static final String INI_INTERNET_SHORTCUT = "InternetShortcut";
    private static final String INI_URL = "URL";
    private static final Charset LATIN1 = Charset.forName("Latin1");
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static void closeAndSaveBundle(Bundle bundle, Path destination)
            throws IOException {
        Path zipPath = closeBundle(bundle);
        // Files.move(zipPath, destination);
        safeMove(zipPath, destination);
    }

    public static Path closeBundle(Bundle bundle) throws IOException {
        Path path = bundle.getSource();
        bundle.close(false);
        return path;
    }

    public static void copyRecursively(final Path source,
            final Path destination, final CopyOption... copyOptions)
            throws IOException {
        final Set<CopyOption> copyOptionsSet = new HashSet<>();
      

        if (!Files.isDirectory(source)) {
            throw new FileNotFoundException("Not a directory: " + source);
        }
        if (Files.isDirectory(destination)
                && !copyOptionsSet
                        .contains(StandardCopyOption.REPLACE_EXISTING)) {
            throw new FileAlreadyExistsException(destination.toString());
        }
        Path destinationParent = destination.getParent();
        if (destinationParent != null && !Files.isDirectory(destinationParent)) {
            throw new FileNotFoundException("Not a directory: "
                    + destinationParent);
        }

        try (RecursiveCopyFileVisitor visitor = new RecursiveCopyFileVisitor(destination,
                copyOptionsSet, source)) {
            Set<FileVisitOption> walkOptions = EnumSet
                    .noneOf(FileVisitOption.class);
            if (!copyOptionsSet.contains(LinkOption.NOFOLLOW_LINKS)) {
                walkOptions = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            }
            Files.walkFileTree(source, walkOptions, Integer.MAX_VALUE, visitor);
        }

    }

    public static Bundle createBundle() throws IOException {
        BundleFileSystem fs = BundleFileSystemProvider
                .newFileSystemFromTemporary();
        return new Bundle(fs.getRootDirectory(), true);
    }

    public static Bundle createBundle(Path path) throws IOException {
        BundleFileSystem fs = BundleFileSystemProvider
                .newFileSystemFromNew(path);
        return new Bundle(fs.getRootDirectory(), true);
    }

    public static void deleteRecursively(Path p) throws IOException {
        if (Files.isDirectory(p)) {
            Files.walkFileTree(p, new RecursiveDeleteVisitor());
        } else {
            Files.delete(p);
        }
    }

    protected static String filenameWithoutExtension(Path entry) {
        String fileName = entry.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot < 0) {
            // return fileName;
            return fileName.replace("/", "");
        }
        return fileName.substring(0, lastDot);
    }

    public static URI getReference(Path path) throws IOException {
        if (path == null || isMissing(path)) {
            return null;
        }
        if (!isReference(path)) {
            throw new IllegalArgumentException("Not a reference: " + path);
        }
        // Note: Latin1 is chosen here because it would not bail out on
        // "strange" characters. We actually parse the URL as ASCII
        path = withExtension(path, DOT_URL);
        try (BufferedReader r = Files.newBufferedReader(path, LATIN1)) {
            HierarchicalINIConfiguration ini = new HierarchicalINIConfiguration();
            ini.load(r);

            String urlStr = ini.getSection(INI_INTERNET_SHORTCUT).getString(
                    INI_URL);

            // String urlStr = ini.get(INI_INTERNET_SHORTCUT, INI_URL);
            if (urlStr == null) {
                throw new IOException("Invalid/unsupported URL format: " + path);
            }
            return URI.create(urlStr);
        } catch (ConfigurationException e) {
            throw new IOException("Can't parse reference: " + path, e);
        }
    }

    public static String getStringValue(Path path) throws IOException {
        if (path == null || isMissing(path)) {
            return null;
        }
        if (!isValue(path)) {
            throw new IllegalArgumentException("Not a value: " + path);
        }
        return new String(Files.readAllBytes(path), UTF8);
    }

    public static boolean isMissing(Path item) {
        return !Files.exists(item) && !isReference(item);
    }

    public static boolean isReference(Path path) {
        return Files.isRegularFile(withExtension(path, DOT_URL));
    }

    public static boolean isValue(Path path) {
        return !isReference(path) && Files.isRegularFile(path);
    }

    public static Bundle openBundle(Path zip) throws IOException {
        BundleFileSystem fs = BundleFileSystemProvider
                .newFileSystemFromExisting(zip);
        return new Bundle(fs.getRootDirectory(), false);
    }

    public static void safeMove(Path source, Path destination)
            throws IOException {

        // First just try to do an atomic move with overwrite
        if (source.getFileSystem().provider()
                .equals(destination.getFileSystem().provider())) {
            try {
                Files.move(source, destination, ATOMIC_MOVE, REPLACE_EXISTING);
                return;
            } catch (AtomicMoveNotSupportedException ex) {
                // Do the fallback by temporary files below
            }
        }

        String tmpName = destination.getFileName().toString();
        Path tmpDestination = Files.createTempFile(destination.getParent(),
                tmpName, ".tmp");
        Path backup = null;
        try {
            // This might do a copy if filestores differ
            // .. hence to avoid an incomplete (and partially overwritten)
            // destination, we do it first to a temporary file
            Files.move(source, tmpDestination, REPLACE_EXISTING);

            if (Files.exists(destination)) {
                // Keep the files for roll-back in case it goes bad
                backup = Files.createTempFile(destination.getParent(), tmpName,
                        ".orig");
                Files.move(destination, backup, REPLACE_EXISTING);
            }
            // OK ; let's swap over:
            try {
                Files.move(tmpDestination, destination, REPLACE_EXISTING,
                        ATOMIC_MOVE);
            } finally {
                if (!Files.exists(destination) && backup != null) {
                    // Restore the backup
                    Files.move(backup, destination);
                }
            }
            // It went well, tidy up
            if (backup != null) {
                Files.deleteIfExists(backup);
            }
        } finally {
            Files.deleteIfExists(tmpDestination);
        }
    }

    public static Path setReference(Path path, URI ref) throws IOException {
        path = withExtension(path, DOT_URL);

        // We'll save a IE-like .url "Internet shortcut" in INI format.

        // HierarchicalINIConfiguration ini = new
        // HierarchicalINIConfiguration();
        // ini.getSection(INI_INTERNET_SHORTCUT).addProperty(INI_URL,
        // ref.toASCIIString());

        // Ini ini = new Wini();
        // ini.getConfig().setLineSeparator("\r\n");
        // ini.put(INI_INTERNET_SHORTCUT, INI_URL, ref.toASCIIString());

        /*
         * Neither of the above create a .url that is compatible with Safari on
         * Mac OS (which expects "URL=" rather than "URL = ", so instead we make
         * it manually with MessageFormat.format:
         */

        // Includes a terminating double line-feed -- which Safari might also
        // need
        String iniTmpl = "[{0}]\r\n{1}={2}\r\n\r\n";
        String ini = MessageFormat.format(iniTmpl, INI_INTERNET_SHORTCUT,
                INI_URL, ref.toASCIIString());

        // NOTE: We use Latin1 here, but because of
        try (BufferedWriter w = Files
                .newBufferedWriter(path, ASCII,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.CREATE)) {
            // ini.save(w);
            // ini.store(w);
            w.write(ini);
            // } catch (ConfigurationException e) {
            // throw new IOException("Can't write shortcut to " + path, e);
        }
        return path;
    }

    public static void setStringValue(Path path, String string)
            throws IOException {
        Files.write(path, string.getBytes(UTF8),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    protected static Path withExtension(Path path, String extension) {
        if (!extension.isEmpty() && !extension.startsWith(".")) {
            throw new IllegalArgumentException(
                    "Extension must be empty or start with .");
        }
        String p = path.getFileName().toString();
        if (!extension.isEmpty()
                && p.toLowerCase().endsWith(extension.toLowerCase())) {
            return path;
        }
        // Everything after the last . - or just the end
        String newP = p.replaceFirst("(\\.[^.]*)?$", extension);
        return path.resolveSibling(newP);
    }

}
