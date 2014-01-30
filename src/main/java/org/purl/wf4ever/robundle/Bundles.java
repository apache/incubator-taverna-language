package org.purl.wf4ever.robundle;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.purl.wf4ever.robundle.utils.PathHelper.relativizeFromBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.purl.wf4ever.robundle.fs.BundleFileSystem;
import org.purl.wf4ever.robundle.fs.BundleFileSystemProvider;
import org.purl.wf4ever.robundle.utils.RecursiveCopyFileVisitor;
import org.purl.wf4ever.robundle.utils.RecursiveDeleteVisitor;
import org.purl.wf4ever.robundle.utils.TemporaryFiles;

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

    private static final String MANIFEST_JSON = "manifest.json";
    private static final String ANNOTATIONS = "annotations";
    private static final String DOT_RO = ".ro";


    protected static final String DOT_URL = ".url";

    private static final Charset ASCII = Charset.forName("ASCII");
    private static final String INI_INTERNET_SHORTCUT = "InternetShortcut";
    private static final String INI_URL = "URL";
    private static final Charset LATIN1 = Charset.forName("Latin1");
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static void closeAndSaveBundle(Bundle bundle, Path destination)
            throws IOException {
        Path zipPath = closeBundle(bundle);
        if (bundle.isDeleteOnClose()) {
            safeMove(zipPath, destination);
        } else {
            safeCopy(zipPath, destination);
        }
    }

    public static Path closeBundle(Bundle bundle) throws IOException {
        Path path = bundle.getSource();
        bundle.close(false);
        return path;
    }

    public static void copyRecursively(final Path source,
            final Path destination, final CopyOption... copyOptions)
            throws IOException {
        RecursiveCopyFileVisitor.copyRecursively(source, destination, copyOptions);
    }

    public static Bundle createBundle() throws IOException {
        BundleFileSystem fs = BundleFileSystemProvider
                .newFileSystemFromTemporary();
        return new Bundle(fs.getRootDirectory(), true);
    }

    public static Bundle createBundle(Path path) throws IOException {
        BundleFileSystem fs = BundleFileSystemProvider
                .newFileSystemFromNew(path);
        return new Bundle(fs.getRootDirectory(), false);
    }

    public static void deleteRecursively(Path p) throws IOException {
        RecursiveDeleteVisitor.deleteRecursively(p);
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

    public static Bundle openBundleReadOnly(Path zip) throws IOException {
        Path tmpBundle = TemporaryFiles.temporaryBundle();
        // BundleFileSystemProvider requires write-access, so we'll have to copy it
        Files.copy(zip, tmpBundle);
        BundleFileSystem fs = BundleFileSystemProvider
                .newFileSystemFromExisting(tmpBundle);
        // And this temporary file will be deleted afterwards
        return new Bundle(fs.getRootDirectory(), true);
    }
    
    public static void safeCopy(Path source, Path destination)
            throws IOException {
        safeMoveOrCopy(source, destination, false);
    }
    
    public static void safeMove(Path source, Path destination)
            throws IOException {
        safeMoveOrCopy(source, destination, true);
    }

    protected static void safeMoveOrCopy(Path source, Path destination, boolean move)
            throws IOException {

        // First just try to do an atomic move with overwrite
        if (move && source.getFileSystem().provider()
                .equals(destination.getFileSystem().provider())) {
            try {
                Files.move(source, destination, ATOMIC_MOVE, REPLACE_EXISTING);
                return;
            } catch (AtomicMoveNotSupportedException ex) {
                // Do the fallback by temporary files below
            }
        }
        
        destination = destination.toAbsolutePath();

        String tmpName = destination.getFileName().toString();
        Path tmpDestination = Files.createTempFile(destination.getParent(),
                tmpName, ".tmp");
        Path backup = null;
        try {
            if (move) {
                // This might do a copy if filestores differ
                // .. hence to avoid an incomplete (and partially overwritten)
                // destination, we do it first to a temporary file
                Files.move(source, tmpDestination, REPLACE_EXISTING);
            } else {
                Files.copy(source, tmpDestination, REPLACE_EXISTING);
            }

            if (Files.exists(destination)) {
                if (Files.isDirectory(destination)) {
                    // ensure it is empty
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(destination)) {
                        for (Path p : ds) {
                            throw new DirectoryNotEmptyException(destination.toString());
                        }
                    }
                }
                // Keep the files for roll-back in case it goes bad
                backup = Files.createTempFile(destination.getParent(), tmpName,
                        ".orig");
                Files.move(destination, backup, REPLACE_EXISTING);
            }
            // OK ; let's swap over
            try {
                // prefer ATOMIC_MOVE
                Files.move(tmpDestination, destination, REPLACE_EXISTING,
                        ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                // possibly a network file system as src/dest should be in same folder
                Files.move(tmpDestination, destination, REPLACE_EXISTING);
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

    public static Path getAnnotations(Bundle dataBundle) throws IOException {
        Path dir = dataBundle.getFileSystem().getPath(DOT_RO, ANNOTATIONS);
        Files.createDirectories(dir);
        return dir;
    }

    public static Path getManifestPath(Bundle bundle) {
        return bundle.getRoot().resolve(DOT_RO).resolve(MANIFEST_JSON);     
    }

    public static String getMimeType(Bundle bundle) throws IOException {
        Path mimetypePath = bundle.getRoot().resolve(BundleFileSystemProvider.MIMETYPE_FILE);
        String mimetype = getStringValue(mimetypePath);
        if (mimetype == null || mimetype.isEmpty()) {
            return BundleFileSystemProvider.APPLICATION_VND_WF4EVER_ROBUNDLE_ZIP;
        }
        return mimetype.trim();
    }
    
    public static void setMimeType(Bundle bundle, String mimetype) throws IOException {
        if (! ASCII.newEncoder().canEncode(mimetype)) { 
            throw new IllegalArgumentException("mimetype must be ASCII, not " + mimetype);
        }
        if (mimetype.contains("\n") || mimetype.contains("\r")) { 
            throw new IllegalArgumentException("mimetype can't contain newlines");
        }
        if (! mimetype.contains("/")) {
            throw new IllegalArgumentException("Invalid mimetype: " + mimetype);
        }
        Path mimetypePath = bundle.getRoot().resolve(BundleFileSystemProvider.MIMETYPE_FILE);
        if (! Files.isRegularFile(mimetypePath)) {
            // It would require low-level zip-modification to properly add 'mimetype' now
            throw new IOException("Special file '" + BundleFileSystemProvider.MIMETYPE_FILE + 
                    "' missing from bundle, can't set mimetype");
        }
        setStringValue(mimetypePath, mimetype);
    }

    public static Path uriToBundlePath(Bundle bundle, URI uri) {
        URI rootUri = bundle.getRoot().toUri();
        uri = relativizeFromBase(uri, rootUri);
        if (uri.isAbsolute() || uri.getFragment() != null) {
            return null;
        }        
        return bundle.getFileSystem().provider().getPath(rootUri.resolve(uri));
    }
    

 



 }
