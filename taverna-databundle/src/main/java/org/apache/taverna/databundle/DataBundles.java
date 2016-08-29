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
 *
 */
package org.apache.taverna.databundle;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.taverna.databundle.DataBundles.ResolveOptions;
import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility functions for dealing with data bundles.
 * <p>
 * The style of using this class is similar to that of {@link Files}. In fact, a
 * data bundle is implemented as a set of {@link Path}s.
 * 
 */
public class DataBundles extends Bundles {
	private static final class OBJECT_MAPPER {
		// Lazy initialization of singleton
		private static final ObjectMapper instance = new ObjectMapper();
	}

	protected static final class ExtensionIgnoringFilter implements
			Filter<Path> {
		private final String fname;

		private ExtensionIgnoringFilter(Path file) {
			this.fname = filenameWithoutExtension(file);
		}

		@Override
		public boolean accept(Path entry) throws IOException {
			return fname.equals(filenameWithoutExtension(entry));
		}
	}

	private static WorkflowBundleIO wfBundleIO;

	private static Logger logger = Logger.getLogger(DataBundles.class.getCanonicalName());

	private static final String WFBUNDLE_CONTENT_TYPE = "application/vnd.taverna.scufl2.workflow-bundle";
	private static final String WFDESC_TURTLE = "text/vnd.wf4ever.wfdesc+turtle";
	private static final String WORKFLOW = "workflow";
	private static final String DOT_WFDESC_TTL = ".wfdesc.ttl";
	private static final String DOT_WFBUNDLE = ".wfbundle";
	private static final String WORKFLOWRUN_PROV_TTL = "workflowrun.prov.ttl";
	private static final String WORKFLOWRUN_JSON = "workflowrun.json";
	private static final String DOT_ERR = ".err";
	private static final String INPUTS = "inputs";
	private static final String INTERMEDIATES = "intermediates";
	private static final String OUTPUTS = "outputs";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	private static Path anyExtension(Path path) throws IOException {
		return anyExtension(path.getParent(), path.getFileName().toString());
	}

	private static Path anyExtension(Path directory, String fileName)
			throws IOException {
		Path path = directory.resolve(fileName);

		// Prefer the fileName as it is
		if (Files.exists(path))
			return path;
		// Strip any existing extension
		String fileNameNoExt = filenameWithoutExtension(path);
		Path withoutExt = path.resolveSibling(fileNameNoExt);
		if (Files.exists(withoutExt))
			return withoutExt;

		// Check directory for path.*
		for (Path p : newDirectoryStream(directory, fileNameNoExt + ".*"))
			/*
			 * We'll just return the first one
			 * 
			 * TODO: Should we fail if there's more than one?
			 */
			return p;

		/*
		 * Nothing? Then let's give the existing one; perhaps it is to be
		 * created.
		 */
		return path;
	}

	private static void checkExistingAnyExtension(Path path)
			throws IOException, FileAlreadyExistsException {
		Path existing = anyExtension(path);
		if (!path.equals(existing))
			throw new FileAlreadyExistsException(existing.toString());
	}

	public static void createList(Path path) throws IOException {
		checkExistingAnyExtension(path);
		Files.createDirectories(path);
	}

	public static void deleteAllExtensions(final Path file) throws IOException {
		Filter<Path> filter = new ExtensionIgnoringFilter(file);
		try (DirectoryStream<Path> ds = newDirectoryStream(file.getParent(),
				filter)) {
			for (Path p : ds)
				deleteRecursively(p);
		}
	}

	protected static String filenameWithoutExtension(Path entry) {
		String fileName = entry.getFileName().toString();
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot < 0)
			return fileName.replace("/", "");
		return fileName.substring(0, lastDot);
	}

	public static ErrorDocument getError(Path path) throws IOException {
		if (path == null)
			return null;

		Path errorPath = withExtension(path, DOT_ERR);
		List<String> errorList = readAllLines(errorPath, UTF8);
		int split = errorList.indexOf("");
		if (split == -1 || errorList.size() <= split)
			throw new IOException("Invalid error document: " + errorPath);

		ErrorDocument errorDoc = new ErrorDocument();

		for (String cause : errorList.subList(0, split))
			errorDoc.getCausedBy().add(path.resolveSibling(cause));

		errorDoc.setMessage(errorList.get(split + 1));

		StringBuilder errorTrace = new StringBuilder();
		for (String line : errorList.subList(split + 2, errorList.size())) {
			errorTrace.append(line);
			errorTrace.append("\n");
		}
		if (errorTrace.length() > 0)
			// Delete last \n
			errorTrace.deleteCharAt(errorTrace.length() - 1);
		errorDoc.setTrace(errorTrace.toString());
		return errorDoc;
	}

	public static Path getInputs(Bundle dataBundle) throws IOException {
		Path inputs = dataBundle.getRoot().resolve(INPUTS);
		createDirectories(inputs);
		return inputs;
	}

	private static long getEntryNumber(Path entry) throws NumberFormatException {
		String name = filenameWithoutExtension(entry);
		return Long.parseLong(name);
	}

	public static List<Path> getList(Path list) throws IOException {
		if (list == null)
			return null;
		List<Path> paths = new ArrayList<>();
		try (DirectoryStream<Path> ds = newDirectoryStream(list)) {
			for (Path entry : ds)
				try {
					long entryNum = getEntryNumber(entry);
					while (paths.size() <= entryNum)
						// Fill any gaps
						paths.add(null);
					// NOTE: Don't use add() as these could come in any order!
					paths.set((int) entryNum, entry);
				} catch (NumberFormatException ex) {
				}
		} catch (DirectoryIteratorException ex) {
			throw ex.getCause();
		}
		return paths;
	}

	public static Path getListItem(Path list, long position) throws IOException {
		if (position < 0)
			throw new IllegalArgumentException(
					"Position must be 0 or more, not: " + position);
		return anyExtension(list, Long.toString(position));
	}

	public static Path getOutputs(Bundle dataBundle) throws IOException {
		Path inputs = dataBundle.getRoot().resolve(OUTPUTS);
		createDirectories(inputs);
		return inputs;
	}

	public static Path getPort(Path map, String portName) throws IOException {
		Files.createDirectories(map);
		return anyExtension(map, portName);
	}

	public static NavigableMap<String, Path> getPorts(Path path)
			throws IOException {
		NavigableMap<String, Path> ports = new TreeMap<>();
		try (DirectoryStream<Path> ds = newDirectoryStream(path)) {
			for (Path p : ds)
				ports.put(filenameWithoutExtension(p), p);
		}
		return ports;
	}

	public static boolean hasInputs(Bundle dataBundle) {
		Path inputs = dataBundle.getRoot().resolve(INPUTS);
		return isDirectory(inputs);
	}

	public static boolean hasOutputs(Bundle dataBundle) {
		Path outputs = dataBundle.getRoot().resolve(OUTPUTS);
		return isDirectory(outputs);
	}

	public static boolean isError(Path path) {
		return isRegularFile(withExtension(path, DOT_ERR));
	}

	public static boolean isList(Path path) {
		return isDirectory(path);
	}

	public static boolean isMissing(Path item) {
		return Bundles.isMissing(item) && !isError(item);
	}

	public static boolean isValue(Path item) {
		return !isError(item) && Bundles.isValue(item);
	}

	public static Path newListItem(Path list) throws IOException {
		createList(list);
		return list.resolve(Long.toString(getListSize(list)));
	}

	public static Path setError(Path path, ErrorDocument error)
			throws IOException {
		return setError(path, error.getMessage(), error.getTrace(), error
				.getCausedBy().toArray(new Path[error.getCausedBy().size()]));
	}

	public static Path setError(Path errorPath, String message, String trace,
			Path... causedBy) throws IOException {
		errorPath = withExtension(errorPath, DOT_ERR);
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
		write(errorPath, errorDoc, UTF8, TRUNCATE_EXISTING, CREATE);
		return errorPath;
	}

	public static Path setReference(Path path, URI reference)
			throws IOException {
		path = withExtension(path, DOT_URL);
		checkExistingAnyExtension(path);
		return Bundles.setReference(path, reference);
	}

	public static void setStringValue(Path path, String string)
			throws IOException {
		checkExistingAnyExtension(path);
		Bundles.setStringValue(path, string);
	}

	protected static Path withExtension(Path path, String extension) {
		String filename = path.getFileName().toString();
		return path.resolveSibling(withExtensionFilename(filename, extension));
	}

	protected static String withExtensionFilename(String filename,
			String extension) {
		if (!extension.isEmpty() && !extension.startsWith("."))
			throw new IllegalArgumentException(
					"Extension must be empty or start with .");
		if (!extension.isEmpty()
				&& filename.toLowerCase().endsWith(extension.toLowerCase()))
			return filename;
		// Everything after the last . - or just the end
		return filename.replaceFirst("(\\.[^.]*)?$", extension);
	}

	public static Path getWorkflowRunProvenance(Bundle dataBundle) {
		return dataBundle.getRoot().resolve(WORKFLOWRUN_PROV_TTL);
	}

	public static Path getWorkflowRunReport(Bundle dataBundle) {
		return dataBundle.getRoot().resolve(WORKFLOWRUN_JSON);
	}

	public static JsonNode getWorkflowRunReportAsJson(Bundle dataBundle)
			throws IOException {
		Path path = getWorkflowRunReport(dataBundle);
		try (InputStream jsonIn = newInputStream(path)) {
			return OBJECT_MAPPER.instance.readTree(jsonIn);
		}
	}

	public static void setWorkflowRunReport(Bundle dataBundle,
			JsonNode workflowRunReport) throws IOException {
		Path path = getWorkflowRunReport(dataBundle);
		try (OutputStream out = newOutputStream(path)) {
			OBJECT_MAPPER.instance.writeValue(out, workflowRunReport);
		}
	}

	public static Path getWorkflow(Bundle dataBundle) throws IOException {
		return anyExtension(dataBundle.getRoot(), WORKFLOW);
	}

	public static Path getWorkflowDescription(Bundle dataBundle)
			throws IOException {
		Path annotations = getAnnotations(dataBundle);
		return annotations.resolve(WORKFLOW + DOT_WFDESC_TTL);
	}

	public static void setWorkflowBundle(Bundle dataBundle,
			WorkflowBundle wfBundle) throws IOException {
		Path bundlePath = withExtension(getWorkflow(dataBundle), DOT_WFBUNDLE);
		checkExistingAnyExtension(bundlePath);

		// TODO: Save as nested folder?
		try (OutputStream outputStream = newOutputStream(bundlePath)) {
			getWfBundleIO().writeBundle(wfBundle, outputStream,
					WFBUNDLE_CONTENT_TYPE);
		} catch (WriterException e) {
			throw new IOException("Can't write workflow bundle to: "
					+ bundlePath, e);
		}

		// wfdesc
		Path wfdescPath = getWorkflowDescription(dataBundle);
		try (OutputStream outputStream = newOutputStream(wfdescPath)) {
			getWfBundleIO().writeBundle(wfBundle, outputStream, WFDESC_TURTLE);
		} catch (IllegalArgumentException | WriterException e) {
			logger.log(Level.WARNING, "Can't write wfdesc to: " + bundlePath, e);
			delete(wfdescPath);
			// throw new IOException("Can't write wfdesc to: " + bundlePath, e);
		}
	}

	public static WorkflowBundle getWorkflowBundle(Bundle dataBundle)
			throws ReaderException, IOException {
		Path wf = getWorkflow(dataBundle);
		// String type = Files.probeContentType(wf);
		return getWfBundleIO().readBundle(newInputStream(wf), null);
	}

	public static Path getIntermediates(Bundle dataBundle) throws IOException {
		Path intermediates = dataBundle.getRoot().resolve(INTERMEDIATES);
		createDirectories(intermediates);
		return intermediates;
	}

	public static Path getIntermediate(Bundle dataBundle, UUID uuid)
			throws IOException {
		String fileName = uuid.toString();
		Path intermediates = getIntermediates(dataBundle);
		// Folder is named after first 2 characters of UUID
		Path folder = intermediates.resolve(fileName.substring(0, 2));
		createDirectories(folder);
		return anyExtension(folder, fileName);
	}

	public static long getListSize(Path list) throws IOException {
		// Should fail if list is not a directory
		try (DirectoryStream<Path> ds = newDirectoryStream(list)) {
			long max = -1L;
			for (Path entry : ds)
				try {
					long entryNum = getEntryNumber(entry);
					if (entryNum > max)
						max = entryNum;
				} catch (NumberFormatException ex) {
				}
			return max + 1;
		} catch (DirectoryIteratorException ex) {
			throw ex.getCause();
		}
	}
	
	public enum ResolveOptions { 
		/**
		 * Leaf values are represented as bundle {@link Path}s, except errors as
		 * {@link ErrorDocument} and references as {@link URL}. Note that specifying this
		 * option does not negate any of the other options like {@link #BYTES}.
		 */
		DEFAULT,
		/**
		 * Leaf values should be represented as a {@link String} (NOTE: This won't work well if the path is a binary)
		 */
		STRING,
		/**
		 * Leaf values should be represented as a <code>byte[]</code>
		 */
		BYTES,
		/**
		 * Leaf values should always be represented as {@link URI}s (except errors)
		 */
		URI,
		/**
		 * Leaf values should be represented as bundle {@link Path}s (even if they are errors)
		 */
		PATH,
		/**
		 * Replace errors with <code>null</code>, or the empty string if {@link #REPLACE_NULL} is also specified.
		 */
		REPLACE_ERRORS,
		/**
		 * Instead of returning <code>null</code>, return the empty
		 * {@link String} "", or empty byte[] if {@link #BYTES} is specified, or
		 * the missing path if {@link #PATH} is specified.
		 */
		REPLACE_NULL
	}
		
	/**
	 * Deeply resolve a {@link Path} to JVM objects.
	 * <p>
	 * This method is intended mainly for presentational uses 
	 * with a particular input/output port from
	 * {@link #getPorts(Path)} or {@link #getPort(Path, String)}.
	 * <p>
	 * Note that as all lists are resolved deeply (including lists of lists)
	 * and when using options {@link ResolveOptions#STRING} or {@link ResolveOptions#BYTES}
	 * the full content of the values are read into memory, this 
	 * method can be time-consuming.
	 * <p>
	 * If the path is <code>null</code> or {@link #isMissing(Path)},
	 * <code>null</code> is returned, unless the option
	 * {@link ResolveOptions#REPLACE_NULL} is specified, which would return the
	 * empty String "".
	 * <p>
	 * If the path {@link #isValue(Path)} and the option
	 * {@link ResolveOptions#STRING} is specified, its
	 * {@link #getStringValue(Path)} is returned (assuming an UTF-8 encoding).
	 * NOTE: Binary formats (e.g. PNG) will NOT be represented correctly read as
	 * UTF-8 String and should instead be read directly with
	 * {@link Files#newInputStream(Path, java.nio.file.OpenOption...)}. Note
	 * that this could consume a large amount of memory as no size checks are
	 * performed.
	 * <p>
	 * If the option {@link ResolveOptions#URI} is specified, all non-missing 
	 * non-error leaf values are resolved as a {@link URI}. If the path is a 
	 * {@link #isReference(Path)} the URI will be the reference from 
	 * {@link #getReference(Path)}, otherwise the URI will  
	 * identify a {@link Path} within the current {@link Bundle}.
	 * <p>
	 * If the path {@link #isValue(Path)} and the option
	 * {@link ResolveOptions#BYTES} is specified, the complete content is returned as
	 * a <code>byte[]</code>. Note that this could consume a large amount of memory
	 * as no size checks are performed.
	 * <p>
	 * If the path {@link #isError(Path)}, the corresponding
	 * {@link ErrorDocument} is returned, except when the option
	 * {@link ResolveOptions#REPLACE_ERRORS} is specified, which means errors are
	 * returned as <code>null</code> (or <code>""</code> if {@link ResolveOptions#REPLACE_NULL} is also specified).
	 * <p>
	 * If the path {@link #isReference(Path)} and the option 
	 * {@link ResolveOptions#URI} is <strong>not</strong> set, 
	 * either a {@link File} or a {@link URL} is returned, 
	 * depending on its protocol. If the reference protocol has no
	 * corresponding {@link URLStreamHandler}, a {@link URI} is returned
	 * instead. 
	 * <p>
	 * If the path {@link #isList(Path)}, a {@link List} is returned
	 * corresponding to resolving the paths from {@link #getList(Path)}. using
	 * this method with the same options.
	 * <p>
	 * If none of the above, the {@link Path} itself is returned. This is 
	 * thus the default for non-reference non-error leaf values if neither 
	 * {@link ResolveOptions#STRING}, {@link ResolveOptions#BYTES} or
	 * {@link ResolveOptions#URI} are specified.
	 * To force returning of {@link Path}s for all non-missing leaf values, specify
	 * {@link ResolveOptions#PATH};
	 * 
	 * @param path
	 *            Data bundle path to resolve
	 * @param options
	 *            Resolve options
	 * @return <code>null</code>, a {@link String}, {@link ErrorDocument},
	 *         {@link URL}, {@link File}, {@link Path} or {@link List}
	 *         (containing any of these) depending on the path type and the options.
	 * @throws IOException
	 *             If the path (or any of the path in a contained list) can't be
	 *             accessed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object resolve(Path path, ResolveOptions... options) throws IOException {
		EnumSet<ResolveOptions> opt;
		if (options.length == 0) {
			opt = EnumSet.of(ResolveOptions.DEFAULT); // no-op
		} else {
			opt = EnumSet.of(ResolveOptions.DEFAULT, options);
		}
		
		if (opt.contains(ResolveOptions.BYTES) && opt.contains(ResolveOptions.STRING)) {
			throw new IllegalArgumentException("Incompatible options: BYTES and STRING");
		}
		if (opt.contains(ResolveOptions.BYTES) && opt.contains(ResolveOptions.PATH)) {
			throw new IllegalArgumentException("Incompatible options: BYTES and PATH");
		}
		if (opt.contains(ResolveOptions.BYTES) && opt.contains(ResolveOptions.URI)) {
			throw new IllegalArgumentException("Incompatible options: BYTES and URI");
		}
		if (opt.contains(ResolveOptions.STRING) && opt.contains(ResolveOptions.PATH)) {
			throw new IllegalArgumentException("Incompatible options: STRING and PATH");
		}
		if (opt.contains(ResolveOptions.STRING) && opt.contains(ResolveOptions.URI)) {
			throw new IllegalArgumentException("Incompatible options: STRING and URI");
		}
		if (opt.contains(ResolveOptions.PATH) && opt.contains(ResolveOptions.URI)) {
			throw new IllegalArgumentException("Incompatible options: PATH and URI");
		}

		
		if (path == null || isMissing(path)) {
			if (! opt.contains(ResolveOptions.REPLACE_NULL)) { 
				return null;
			}
			if (opt.contains(ResolveOptions.BYTES)) {
				return new byte[0];
			}
			if (opt.contains(ResolveOptions.PATH)) { 
				return path;
			}
			if (opt.contains(ResolveOptions.URI)) {
				return path.toUri();
			}
			// STRING and DEFAULT
			return "";			
			
 
		}
		
		if (isList(path)) {
			List<Path> list = getList(path);
			List<Object> objectList = new ArrayList<Object>(list.size());
			for (Path pathElement : list) {
				objectList.add(resolve(pathElement, options));
			}
			return objectList;
		}		
		if (opt.contains(ResolveOptions.PATH)) {
			return path;
		}		
		if (isError(path)) {
			if (opt.contains(ResolveOptions.REPLACE_ERRORS)) {
				return opt.contains(ResolveOptions.REPLACE_NULL) ? "" : null;	
			}
			return getError(path);
		}
		if (opt.contains(ResolveOptions.URI)) {
			if (isReference(path)) {
				return getReference(path);
			} else {
				return path.toUri();
			}
		}
		if (isReference(path)) {
			URI reference = getReference(path);
			String scheme = reference.getScheme();
			if ("file".equals(scheme)) {
				return new File(reference);
			} else {
				try { 
					return reference.toURL();
				} catch (IllegalArgumentException|MalformedURLException e) {
					return reference;
				}
			}
		}
		if (isValue(path)) {
			if (opt.contains(ResolveOptions.BYTES)) {
				return Files.readAllBytes(path);
			}
			if (opt.contains(ResolveOptions.STRING)) {
				return getStringValue(path);
			}
		}
		// Fall-back - return Path as-is
		return path;
	}

	/**
	 * Deeply resolve path as a {@link Stream} that only contain leaf elements of 
	 * the specified class.
	 * <p>
	 * This method is somewhat equivalent to {@link #resolve(Path, ResolveOptions...)}, but 
	 * the returned stream is not in any particular order, and will contain the leaf
	 * items from all deep lists. Empty lists and error documents are ignored.
	 * <p>
	 * Any {@link IOException}s occurring during resolution are 
	 * wrapped as {@link UncheckedIOException}.
	 * <p>
	 * Supported types include:
	 * <ul>
	 *   <li>{@link String}.class</li>
	 *   <li><code>byte[].class</code></li>
	 *   <li>{@link Path}.class</li>
	 *   <li>{@link URI}.class</li>
	 *   <li>{@link URL}.class</li>  
	 *   <li>{@link File}.class</li>
	 *   <li>{@link ErrorDocument}.class</li>
	 *   <li>{@link Object}.class</li>
	 * </ul>
	 * 
	 * @param path Data bundle path to resolve
	 * @param type Type of objects to return, e.g. <code>String.class</code>
	 * @return A {@link Stream} of resolved objects, or an empty stream if no such objects were resolved.
	 * @throws UncheckedIOException If the path could not be accessed. 
	 */
	public static <T> Stream<T> resolveAsStream(Path path, Class<T> type) throws UncheckedIOException {
		ResolveOptions options;
		if (type == String.class) {
			options = ResolveOptions.STRING;
		} else if (type == byte[].class) {
			options = ResolveOptions.BYTES;
		} else if (type == Path.class) {
			options = ResolveOptions.PATH;
		} else if (type == URI.class) {
			options = ResolveOptions.URI;
		} else {
			// Dummy-option, we'll filter on the returned type anyway
			options = ResolveOptions.DEFAULT;
		}
		if (isList(path)) {
			// return Stream of unordered list of resolved list items,	
			// recursing to find the leaf nodes			
			try {
				return Files.walk(path)
						// avoid re-recursion
						.filter(p -> !Files.isDirectory(p)) 
						.flatMap(p -> resolveItemAsStream(p, type, options));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			return resolveItemAsStream(path, type, options);
		}
	}
	private static <T> Stream<T> resolveItemAsStream(Path path, Class<T> type, ResolveOptions options) throws UncheckedIOException {
		try {
			Object value = resolve(path, options);
			if (type.isInstance(value)) {
				return Stream.of(type.cast(value));
			}
			return Stream.empty();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static WorkflowBundleIO getWfBundleIO() {
		if (wfBundleIO == null)
			wfBundleIO = new WorkflowBundleIO();
		return wfBundleIO;
	}

	public static void setWfBundleIO(WorkflowBundleIO wfBundleIO) {
		if (wfBundleIO == null)
			throw new NullPointerException();
		DataBundles.wfBundleIO = wfBundleIO;
	}
}
