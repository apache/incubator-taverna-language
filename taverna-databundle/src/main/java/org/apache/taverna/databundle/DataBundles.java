package org.apache.taverna.databundle;
/*
 *
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility functions for dealing with data bundles.
 * <p>
 * The style of using this class is similar to that of {@link Files}. In fact, a
 * data bundle is implemented as a set of {@link Path}s.
 * 
 * @author Stian Soiland-Reyes
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

	private static Logger logger = Logger.getLogger(DataBundles.class);

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
			logger.warn("Can't write wfdesc to: " + bundlePath, e);
			delete(wfdescPath);
			// throw new IOException("Can't write wfdesc to: " + bundlePath, e);
		}
	}

	public static WorkflowBundle getWorkflowBundle(Bundle dataBundle)
			throws ReaderException, IOException {
		Path wf = getWorkflow(dataBundle);
		// String type = Files.probeContentType(wf);
		return wfBundleIO.readBundle(newInputStream(wf), null);
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
