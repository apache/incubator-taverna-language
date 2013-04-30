package uk.org.taverna.databundle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility functions for dealing with data bundles.
 * <p>
 * The style of using this class is similar to that of {@link Files}. In fact, a
 * data bundle is implemented as a set of {@link Path}s.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class DataBundles {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String INPUTS = "inputs";
	private static final String OUTPUTS = "outputs";

	public static Path createDataBundle() throws IOException {

		// Create ZIP file as http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
		
		Path dataBundle = Files.createTempFile("databundle", ".robundle.zip");
		
		FileSystem fs = createFSfromZip(dataBundle);
//		FileSystem fs = createFSfromJar(dataBundle);		
		return fs.getRootDirectories().iterator().next();
		//return Files.createTempDirectory("databundle");
	}

	protected static FileSystem createFSfromZip(Path dataBundle)
			throws FileNotFoundException, IOException {
		ZipOutputStream out = new ZipOutputStream(
			    new FileOutputStream(dataBundle.toFile()));
		ZipEntry mimeTypeEntry = new ZipEntry("mimetype");
		out.putNextEntry(mimeTypeEntry);
		out.closeEntry();
		out.close();
		return FileSystems.newFileSystem(dataBundle,  null);
	}

	protected static FileSystem createFSfromJar(Path path)
			throws IOException {
		Files.deleteIfExists(path);
		URI uri;
		try {
			uri = new URI("jar", path.toUri().toASCIIString(), null);
		} catch (URISyntaxException e) {
			throw new IOException("Can't make jar: URI using " + path.toUri());
		}		
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		return FileSystems.newFileSystem(uri, env);
	}

	public static Path getInputs(Path dataBundle) throws IOException {
		Path inputs = dataBundle.resolve(INPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}

	public static Path getOutputs(Path dataBundle) throws IOException {
		Path inputs = dataBundle.resolve(OUTPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}

	public static boolean hasInputs(Path dataBundle) {
		Path inputs = dataBundle.resolve(INPUTS);
		return Files.isDirectory(inputs);
	}

	public static boolean hasOutputs(Path dataBundle) {
		Path outputs = dataBundle.resolve(OUTPUTS);
		return Files.isDirectory(outputs);
	}

	public static Path getPort(Path map, String portName) throws IOException {
		Files.createDirectories(map);
		return map.resolve(portName);
	}

	public static void setStringValue(Path path, String string) throws IOException {		
		Files.write(path, string.getBytes(UTF8));
	}

	public static String getStringValue(Path path) throws IOException {
		return new String(Files.readAllBytes(path), UTF8);
	}

	public static void createList(Path path) throws IOException {
		Files.createDirectories(path);
	}

	public static Path newListItem(Path list) throws IOException {
		long max = -1L;
		createList(list);
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(list)) {
			for (Path entry : ds) {
				String name = filenameWithoutExtension(entry);
				//System.out.println(name);
				try {
					long entryNum = Long.parseLong(name);
					if (entryNum > max) {
						max = entryNum;
					}
				} catch (NumberFormatException ex) {
				}
			}
		} catch (DirectoryIteratorException ex) {
			throw ex.getCause();
		}
		return list.resolve(Long.toString(max+1));
	}

	protected static String filenameWithoutExtension(Path entry) {
		String fileName = entry.getFileName().toString();
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot < 0) {
			return fileName;
		}
		return fileName.substring(0, lastDot);
	}

	public static boolean isList(Path list) {
		return Files.isDirectory(list);
	}

	public static List<Path> getList(Path list) throws IOException {
		List<Path> paths = new ArrayList<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(list)) {
			for (Path entry : ds) {
				String name = filenameWithoutExtension(entry);
				//System.out.println(name);
				try {
					int entryNum = Integer.parseInt(name);
					while (paths.size() <= entryNum) {
						// Fill any gaps
						paths.add(null);
					}
					// NOTE: Don't use add() as these could come in any order!
					paths.set(entryNum, entry);					
				} catch (NumberFormatException ex) {
				}
			}
		} catch (DirectoryIteratorException ex) {
			throw ex.getCause();
		}
		return paths;		
	}
}
