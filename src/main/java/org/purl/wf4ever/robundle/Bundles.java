package org.purl.wf4ever.robundle;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;

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

	private static final Charset ASCII = Charset.forName("ASCII");
	private static final String INI_INTERNET_SHORTCUT = "InternetShortcut";
	private static final String INI_URL = "URL";
	private static final Charset LATIN1 = Charset.forName("Latin1");
	private static final String URL = ".url";
	private static final Charset UTF8 = Charset.forName("UTF-8");


	public static void closeAndSaveBundle(Bundle bundle,
			Path destination) throws IOException {
		Path zipPath = closeBundle(bundle);
		// Files.move(zipPath, destination);
		safeMove(zipPath, destination);
	}

	public static Path closeBundle(Bundle bundle)
			throws IOException {
		Path path = bundle.getSource();
		bundle.close(false);
		return path;
	}

	public static Bundle createBundle() throws IOException {
		Path bundle = Files.createTempFile("robundle", ".zip");		
		BundleFileSystem fs = BundleFileSystemProvider.newFileSystemFromNew(bundle, null);
		// FileSystem fs = createFSfromJar(bundle);
		return new Bundle(fs.getRootDirectory(), true);
		// return Files.createTempDirectory("bundle");
	}


	protected static String filenameWithoutExtension(Path entry) {
		String fileName = entry.getFileName().toString();
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot < 0) {	
//			return fileName;
			return fileName.replace("/", "");
		}
		return fileName.substring(0, lastDot);
	}

	public static URI getReference(Path path) throws IOException {
		if (path == null || isMissing(path)) {
			return null;
		}	
		if (! isReference(path)) {
			throw new IllegalArgumentException("Not a reference: " + path);
		}
		// Note: Latin1 is chosen here because it would not bail out on 
		// "strange" characters. We actually parse the URL as ASCII
		path = withExtension(path, ".url");
		try (BufferedReader r = Files.newBufferedReader(path, LATIN1)) {
			HierarchicalINIConfiguration ini = new HierarchicalINIConfiguration();
			ini.load(r);
			
			String urlStr = ini.getSection(INI_INTERNET_SHORTCUT).getString(INI_URL);
			
//			String urlStr = ini.get(INI_INTERNET_SHORTCUT, INI_URL);
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
		if (! isValue(path)) {
			throw new IllegalArgumentException("Not a value: " + path);
		}
		return new String(Files.readAllBytes(path), UTF8);
	}
	
	public static boolean isMissing(Path item) {
	//		if (! Files.exists(item.getParent())) {
	//			throw new IllegalStateException("Invalid path");
	//		}
			return ! Files.exists(item) && !isReference(item);
		}

	public static boolean isReference(Path path) {
		return Files.isRegularFile(withExtension(path, URL));
	}

	public static boolean isValue(Path path) {
		return Files.isRegularFile(path);
	}

	public static Bundle openBundle(Path zip) throws IOException {
		BundleFileSystem fs = BundleFileSystemProvider.newFileSystemFromExisting(zip);		
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
		path = withExtension(path, ".url");

		// We'll save a IE-like .url "Internet shortcut" in INI format.
		
		
//		HierarchicalINIConfiguration ini = new HierarchicalINIConfiguration();
//		ini.getSection(INI_INTERNET_SHORTCUT).addProperty(INI_URL,
//				ref.toASCIIString());

//		Ini ini = new Wini();
//		ini.getConfig().setLineSeparator("\r\n");
//		ini.put(INI_INTERNET_SHORTCUT, INI_URL, ref.toASCIIString());		 

		/*
		 * Neither of the above create a .url that is compatible with Safari on
		 * Mac OS (which expects "URL=" rather than "URL = ", so instead we make
		 * it manually with MessageFormat.format:
		 */
		
		// Includes a terminating double line-feed -- which Safari might also need
		String iniTmpl = "[{0}]\r\n{1}={2}\r\n\r\n";
		String ini = MessageFormat.format(iniTmpl, 
				INI_INTERNET_SHORTCUT, INI_URL, ref.toASCIIString());
		
		
		
		// NOTE: We use Latin1 here, but because of 
		try (BufferedWriter w = Files
				.newBufferedWriter(path, ASCII,
						StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.CREATE)) {			
			// ini.save(w);
			// ini.store(w);
			w.write(ini);
//		} catch (ConfigurationException e) {
//			throw new IOException("Can't write shortcut to " + path, e);
		}
		return path;
	}

	public static void setStringValue(Path path, String string)
			throws IOException {
		Files.write(path, string.getBytes(UTF8), 
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}

	protected static Path withExtension(Path path, String extension) {
		if (! extension.isEmpty() && ! extension.startsWith(".")) {
			throw new IllegalArgumentException("Extension must be empty or start with .");
		}
		String p = path.getFileName().toString();
		if (! extension.isEmpty() && p.toLowerCase().endsWith(extension.toLowerCase())) {
			return path;
		}		
		// Everything after the last . - or just the end
		String newP = p.replaceFirst("(\\.[^.]*)?$", extension);
		return path.resolveSibling(newP);
	}

}
