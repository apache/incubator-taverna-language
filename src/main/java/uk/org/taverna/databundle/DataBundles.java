package uk.org.taverna.databundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

/**
 * Utility functions for dealing with data bundles.
 * <p>
 * The style of using this class is similar to that of {@link Files}. In fact, a
 * data bundle is implemented as a set of {@link Path}s.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class DataBundles extends Bundles {

	private static final Charset ASCII = Charset.forName("ASCII");
	private static final String ERR = ".err";
	private static final String INI_INTERNET_SHORTCUT = "InternetShortcut";
	private static final String INI_URL = "URL";
	private static final String INPUTS = "inputs";
	private static final Charset LATIN1 = Charset.forName("Latin1");
	private static final String OUTPUTS = "outputs";
	private static final String URL = ".url";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static void createList(Path path) throws IOException {
		Files.createDirectories(path);
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

	public static ErrorDocument getError(Path path) throws IOException {
		if (path == null) {
			return null;
		}
			
		Path errorPath = withExtension(path, ERR);
		List<String> errorList = Files.readAllLines(errorPath, UTF8);
		int split = errorList.indexOf("");
		if (split == -1 || errorList.size() <= split) {
			throw new IOException("Invalid error document: " + errorPath);
		}
		
		ErrorDocument errorDoc = new ErrorDocument();

		for (String cause : errorList.subList(0, split)) {
			errorDoc.getCausedBy().add(path.resolveSibling(cause));
		}
		
		errorDoc.setMessage(errorList.get(split+1));
		
		StringBuffer errorTrace = new StringBuffer();
		for (String line : errorList.subList(split+2, errorList.size())) {
			errorTrace.append(line);
			errorTrace.append("\n");	
		}		
		if (errorTrace.length() > 0) { 
			// Delete last \n
			errorTrace.deleteCharAt(errorTrace.length()-1);
		}
		errorDoc.setTrace(errorTrace.toString());
		return errorDoc;
	}

	public static Path getInputs(Bundle dataBundle) throws IOException {
		Path inputs = dataBundle.getRoot().resolve(INPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}

	public static List<Path> getList(Path list) throws IOException {
		if (list == null) {
			return null;
		}
		List<Path> paths = new ArrayList<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(list)) {
			for (Path entry : ds) {
				String name = filenameWithoutExtension(entry);
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

	public static Path getListItem(Path list, long position) {
		if (position < 0) {
			throw new IllegalArgumentException("Position must be 0 or more, not: " + position);
		}
		// FIXME: Look for extensions
		return list.resolve(Long.toString(position));
	}

	public static Path getOutputs(Bundle dataBundle) throws IOException {
		Path inputs = dataBundle.getRoot().resolve(OUTPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}

	public static Path getPort(Path map, String portName) throws IOException {
		Files.createDirectories(map);
		return map.resolve(portName);
	}

	public static NavigableMap<String, Path> getPorts(Path path) throws IOException {
		NavigableMap<String, Path> ports = new TreeMap<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			for (Path p : ds) {
				ports.put(filenameWithoutExtension(p), p);
			}
		}
		return ports;
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
	
	public static boolean hasInputs(Bundle dataBundle) {
		Path inputs = dataBundle.getRoot().resolve(INPUTS);
		return Files.isDirectory(inputs);
	}


	public static boolean hasOutputs(Bundle dataBundle) {
		Path outputs = dataBundle.getRoot().resolve(OUTPUTS);
		return Files.isDirectory(outputs);
	}

	public static boolean isError(Path path) {
		return Files.isRegularFile(withExtension(path, ERR));
	}


	
	public static boolean isList(Path path) {
		return Files.isDirectory(path);
	}

	public static boolean isMissing(Path item) {
	//		if (! Files.exists(item.getParent())) {
	//			throw new IllegalStateException("Invalid path");
	//		}
			return ! Files.exists(item) && ! isError(item) && !isReference(item);
		}

	public static boolean isReference(Path path) {
		return Files.isRegularFile(withExtension(path, URL));
	}

	public static boolean isValue(Path path) {
		return Files.isRegularFile(path);
	}

	public static Path newListItem(Path list) throws IOException {
		long max = -1L;
		createList(list);
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(list)) {
			for (Path entry : ds) {
				String name = filenameWithoutExtension(entry);
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
		return list.resolve(Long.toString(max + 1));
	}

	public static Path setError(Path path, ErrorDocument error) throws IOException {
		return setError(path, error.getMessage(), error.getTrace(), error
				.getCausedBy().toArray(new Path[error.getCausedBy().size()]));
	}

	public static Path setError(Path errorPath, String message, String trace, Path... causedBy) throws IOException {
		errorPath = withExtension(errorPath, ERR);
		// Silly \n-based format
		List<String> errorDoc = new ArrayList<>();
		for (Path cause : causedBy) {
			Path relCause = errorPath.getParent().relativize(cause);
			errorDoc.add(relCause.toString());			
		}
		errorDoc.add(""); // Our magic separator
		errorDoc.add(message);
		errorDoc.add(trace);
		Files.write(errorPath, errorDoc, UTF8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		return errorPath;
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
