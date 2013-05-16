package uk.org.taverna.databundle;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

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

	private static final String ERR = ".err";
	private static final String INPUTS = "inputs";
	private static final String OUTPUTS = "outputs";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static void createList(Path path) throws IOException {
	    checkExistingAnyExtension(path);
		Files.createDirectories(path);
	}

    private static void checkExistingAnyExtension(Path path) throws IOException,
            FileAlreadyExistsException {
        Path existing = anyExtension(path);
	    if (! path.equals(existing)) {
	        throw new FileAlreadyExistsException(existing.toString());
	    }
    }
	
	protected static String filenameWithoutExtension(Path entry) {
		String fileName = entry.getFileName().toString();
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot < 0) {	
//			return fileName;
		    // Might be root
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

	public static Path getListItem(Path list, long position) throws IOException {
		if (position < 0) {
			throw new IllegalArgumentException("Position must be 0 or more, not: " + position);
		}
		return anyExtension(list, Long.toString(position));
	}

	public static Path getOutputs(Bundle dataBundle) throws IOException {
		Path inputs = dataBundle.getRoot().resolve(OUTPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}

	public static Path getPort(Path map, String portName) throws IOException {
		Files.createDirectories(map);
		return anyExtension(map, portName);
	}

	private static Path anyExtension(Path path) throws IOException {
	    return anyExtension(path.getParent(), path.getFileName().toString());
    }
	
    private static Path anyExtension(Path directory, String fileName) throws IOException {
        Path path = directory.resolve(fileName);
		
		// Prefer the fileName as it is
        if (Files.exists(path)) {
		    return path;
		}
        // Strip any existing extension
        String fileNameNoExt = filenameWithoutExtension(path);     
        Path withoutExt = path.resolveSibling(fileNameNoExt);
        if (Files.exists(withoutExt)) {
            return withoutExt;
        }
        
        // Check directory for path.*
        for (Path p : Files.newDirectoryStream(directory, fileNameNoExt + ".*")) {
            // We'll just return the first one
            // TODO: Should we fail if there's more than one?
            return p;
        }
        // Nothing? Then let's give the existing one; perhaps it is to be
        // created.
        return path;
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
		return Bundles.isMissing(item) && ! isError(item);
	}

   public static boolean isValue(Path item) {
        return ! isError(item) && Bundles.isValue(item);
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
		checkExistingAnyExtension(errorPath);
		Files.write(errorPath, errorDoc, UTF8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		return errorPath;
	}

	protected static Path withExtension(Path path, String extension) {
		String filename = path.getFileName().toString();
		return path.resolveSibling(withExtensionFilename(filename, extension));
	}
	
	protected static String withExtensionFilename(String filename, String extension) {
        if (! extension.isEmpty() && ! extension.startsWith(".")) {
            throw new IllegalArgumentException("Extension must be empty or start with .");
        }
        if (! extension.isEmpty() && filename.toLowerCase().endsWith(extension.toLowerCase())) {
            return filename;
        }
        // Everything after the last . - or just the end
        return filename.replaceFirst("(\\.[^.]*)?$", extension);
    }

}
