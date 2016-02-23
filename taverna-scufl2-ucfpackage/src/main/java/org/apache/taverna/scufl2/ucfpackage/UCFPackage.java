package org.apache.taverna.scufl2.ucfpackage;
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


import static java.io.File.createTempFile;
import static java.util.logging.Level.INFO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.taverna.robundle.manifest.odf.ODFJaxb;
import org.apache.taverna.scufl2.ucfpackage.impl.odfdom.pkg.OdfPackage;
import org.apache.taverna.scufl2.ucfpackage.impl.odfdom.pkg.manifest.OdfFileEntry;
import org.apache.taverna.robundle.xml.odf.container.Container;
import org.apache.taverna.robundle.xml.odf.container.Container.RootFiles;
import org.apache.taverna.robundle.xml.odf.container.ObjectFactory;
import org.apache.taverna.robundle.xml.odf.container.RootFile;
import org.w3c.dom.Document;


public class UCFPackage extends ODFJaxb implements Cloneable  {
	private static Logger logger = Logger.getLogger(UCFPackage.class.getName());
	private static final String CONTAINER_XML = "META-INF/container.xml";
	private static final Charset UTF_8 = Charset.forName("utf-8");
	public static final String MIME_BINARY = "application/octet-stream";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String MIME_TEXT_XML = "text/xml";
	public static final String MIME_RDF = "application/rdf+xml";
	public static final String MIME_EPUB = "application/epub+zip";
	public static final String MIME_WORKFLOW_BUNDLE = "application/vnd.taverna.workflow-bundle";
	public static final String MIME_DATA_BUNDLE = "application/vnd.taverna.data-bundle";
	public static final String MIME_WORKFLOW_RUN_BUNDLE = "application/vnd.taverna.workflow-run-bundle";
	public static final String MIME_SERVICE_BUNDLE = "application/vnd.taverna.service-bundle";

	private static Charset ASCII = Charset.forName("ascii");
	private OdfPackage odfPackage;
	private static JAXBContext jaxbContext;
	private JAXBElement<Container> containerXml;
	private boolean createdContainerXml = false;
	private static ObjectFactory containerFactory = new ObjectFactory();

	public UCFPackage() throws IOException {
		try {
			odfPackage = OdfPackage.create();
			parseContainerXML();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not create empty UCF Package", e);
		}
		// odfPackage.setMediaType(MIME_EPUB);
	}

	public UCFPackage(File containerFile) throws IOException {
		open(containerFile);
	}

	protected void open(File containerFile) throws IOException {
		try (BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(containerFile))) {
			open(stream);
		}
	}

	public UCFPackage(InputStream inputStream) throws IOException {
		open(inputStream);
	}

	protected void open(InputStream inputStream) throws IOException {
		try {
			odfPackage = OdfPackage.loadPackage(inputStream);
			parseContainerXML();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(
					"Could not load UCF Package from input stream", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void parseContainerXML() throws IOException {
		createdContainerXml = false;
		InputStream containerStream = getResourceAsInputStream(CONTAINER_XML);
		if (containerStream == null) {
			// Make an empty containerXml
			Container container = containerFactory.createContainer();
			containerXml = containerFactory.createContainer(container);
			createdContainerXml = true;
			return;
		}
		try {
			Unmarshaller unMarshaller = createUnMarshaller();
			containerXml = (JAXBElement<Container>) unMarshaller
					.unmarshal(containerStream);
		} catch (JAXBException e) {
			throw new IOException("Could not parse " + CONTAINER_XML, e);
		}
	}

	public String getPackageMediaType() {
		return odfPackage.getMediaType();
	}

	public void setPackageMediaType(String mediaType) {
		if (mediaType == null || !mediaType.contains("/"))
			throw new IllegalArgumentException("Invalid media type "
					+ mediaType);
		if (!ASCII.newEncoder().canEncode(mediaType))
			throw new IllegalArgumentException("Media type must be ASCII: "
					+ mediaType);
		odfPackage.setMediaType(mediaType);
	}

	public void save(File packageFile) throws IOException {
		File tempFile = createTempFile("." + packageFile.getName(), ".tmp",
				packageFile.getCanonicalFile().getParentFile());
		prepareAndSave(tempFile);
		boolean renamed = tempFile.renameTo(packageFile);
		if (!renamed && packageFile.exists() && tempFile.exists()) {
			// Could happen on Windows
			if (!packageFile.delete())
				// Could have been permission problem
				throw new IOException("Could not delete existing "
						+ packageFile);
			renamed = tempFile.renameTo(packageFile);
		}
		if (!renamed)
			throw new IOException("Could not rename temp file " + tempFile
					+ " to " + packageFile);
	}

	protected void prepareAndSave(File tempFile) throws IOException {
		if (getPackageMediaType() == null)
			throw new IllegalStateException("Package media type must be set");

		// Write using temp file, and do rename in the end

		try {
			prepareContainerXML();
			odfPackage.save(tempFile);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not save bundle to " + tempFile, e);
		} finally {
			odfPackage.close();
		}

		try {
			open(tempFile);
		} catch (Exception e) {
			throw new IOException("Could not reload package from " + tempFile,
					e);
		}
	}

	protected void prepareContainerXML() throws IOException {
		if (containerXml == null || createdContainerXml
				&& containerXml.getValue().getRootFilesOrAny() == null)
			return;

		/* Check if we should prune <rootFiles> */
		Iterator<Object> iterator = containerXml.getValue().getRootFilesOrAny()
				.iterator();
		boolean foundAlready = false;
		while (iterator.hasNext()) {
			Object anyOrRoot = iterator.next();
			if (!(anyOrRoot instanceof JAXBElement))
				continue;
			@SuppressWarnings("rawtypes")
			JAXBElement elem = (JAXBElement) anyOrRoot;
			if (!elem.getDeclaredType().equals(RootFiles.class))
				continue;
			RootFiles rootFiles = (RootFiles) elem.getValue();
			if (foundAlready
					|| (rootFiles.getOtherAttributes().isEmpty() && rootFiles
							.getAnyOrRootFile().isEmpty())) {
				// Delete it!
				System.err.println("Deleting unneccessary <rootFiles>");
				iterator.remove();
			}
			foundAlready = true;
		}

		Marshaller marshaller;
		try {
			marshaller = createMarshaller();
			// XMLStreamWriter xmlStreamWriter = XMLOutputFactory
			// .newInstance().createXMLStreamWriter(outStream);
			// xmlStreamWriter.setDefaultNamespace(containerElem.getName()
			// .getNamespaceURI());
			//
			// xmlStreamWriter.setPrefix("dsig",
			// "http://www.w3.org/2000/09/xmldsig#");
			// xmlStreamWriter.setPrefix("xmlenc",
			// "http://www.w3.org/2001/04/xmlenc#");
			try (OutputStream outStream = odfPackage
					.insertOutputStream(CONTAINER_XML)) {
				// FIXME: Set namespace prefixes and default namespace
				marshaller.setProperty("jaxb.formatted.output", true);
				// TODO: Ensure using default namespace
				marshaller.marshal(containerXml, outStream);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not parse " + CONTAINER_XML, e);
		}
	}

	public void addResource(String stringValue, String path, String mediaType)
			throws IOException {
		try {
			odfPackage.insert(stringValue.getBytes(UTF_8), path, mediaType);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not add " + path, e);
		}
		parseContainerXML();
	}

	public void addResource(byte[] bytesValue, String path, String mediaType)
			throws IOException {
		try {
			odfPackage.insert(bytesValue, path, mediaType);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not add " + path, e);
		}
		if (path.equals(CONTAINER_XML))
			parseContainerXML();
	}

	public void addResource(Document document, String path, String mediaType)
			throws IOException {
		try {
			odfPackage.insert(document, path, mediaType);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not add " + path, e);
		}
		if (path.equals(CONTAINER_XML))
			parseContainerXML();
	}

	public void addResource(InputStream inputStream, String path,
			String mediaType) throws IOException {
		try {
			odfPackage.insert(inputStream, path, mediaType);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not add " + path, e);
		}
		if (path.equals(CONTAINER_XML))
			parseContainerXML();
	}

	public void addResource(URI uri, String path, String mediaType)
			throws IOException {
		try {
			odfPackage.insert(uri, path, mediaType);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not add " + path, e);
		}

		if (path.equals(CONTAINER_XML))
			parseContainerXML();
	}

	public String getResourceAsString(String path) throws IOException {
		try {
			return new String(odfPackage.getBytes(path), UTF_8);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not get " + path, e);
		}
	}

	public byte[] getResourceAsBytes(String path) throws IOException {
		try {
			return odfPackage.getBytes(path);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not get " + path, e);
		}
	}

	public InputStream getResourceAsInputStream(String path) throws IOException {
		try {
			return odfPackage.getInputStream(path);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not get " + path, e);
		}
	}

	public Map<String, ResourceEntry> listResources() {
		return listResources("", false);
	}

	public Map<String, ResourceEntry> listResources(String folderPath) {
		return listResources(folderPath, false);
	}

	protected Map<String, ResourceEntry> listResources(String folderPath,
			boolean recursive) {
		if (!folderPath.isEmpty() && !folderPath.endsWith("/"))
			folderPath = folderPath + "/";
		HashMap<String, ResourceEntry> content = new HashMap<>();

		for (Entry<String, OdfFileEntry> entry : odfPackage
				.getManifestEntries().entrySet()) {
			String entryPath = entry.getKey();
			if (!entryPath.startsWith(folderPath))
				continue;
			String subPath = entryPath.substring(folderPath.length(),
					entryPath.length());
			if (subPath.isEmpty())
				// The folder itself
				continue;
			int firstSlash = subPath.indexOf("/");
			if (!recursive && firstSlash > -1
					&& firstSlash < subPath.length() - 1)
				/*
				 * Children of a folder (note that we'll include the folder
				 * itself which ends in /)
				 */
				continue;
			content.put(subPath, new ResourceEntry(entry.getValue()));
		}
		return content;
	}

	public void removeResource(String path) {
		if (!odfPackage.contains(path))
			return;
		if (path.endsWith("/"))
			for (ResourceEntry childEntry : listResources(path).values())
				removeResource(childEntry.getPath());
		odfPackage.remove(path);
	}

	public class ResourceEntry {
		private final String path;
		private final long size;
		private String mediaType;
		private String version;

		protected ResourceEntry(OdfFileEntry odfEntry) {
			path = odfEntry.getPath();
			size = odfEntry.getSize();
			mediaType = odfEntry.getMediaType();
			version = odfEntry.getVersion();
		}

		public String getPath() {
			return path;
		}

		public long getSize() {
			return size;
		}

		public String getMediaType() {
			return mediaType;
		}

		public boolean isFolder() {
			return path.endsWith("/");
		}

		public UCFPackage getUcfPackage() {
			return UCFPackage.this;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ResourceEntry))
				return false;
			ResourceEntry other = (ResourceEntry) obj;

			if (!getUcfPackage().equals(other.getUcfPackage()))
				return false;
			return getPath().equals(other.getPath());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getUcfPackage().hashCode();
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

		public String getVersion() {
			return version;
		}
	}

	public Map<String, ResourceEntry> listAllResources() {
		return listResources("", true);
	}

	public void setRootFile(String path) {
		setRootFile(path, null);
	}

	@SuppressWarnings("rawtypes")
	public void setRootFile(String path, String version) {
		ResourceEntry rootFile = getResourceEntry(path);
		if (rootFile == null)
			throw new IllegalArgumentException("Unknown resource: " + path);
		odfPackage.getManifestEntries().get(path).setVersion(version);

		Container container = containerXml.getValue();

		RootFiles rootFiles = getRootFiles(container);
		String mediaType = rootFile.getMediaType();
		boolean foundExisting = false;
		// Check any existing files for matching path/mime type
		Iterator<Object> anyOrRootIt = rootFiles.getAnyOrRootFile().iterator();
		while (anyOrRootIt.hasNext()) {
			Object anyOrRoot = anyOrRootIt.next();
			if (anyOrRoot instanceof JAXBElement)
				anyOrRoot = ((JAXBElement) anyOrRoot).getValue();
			if (!(anyOrRoot instanceof RootFile))
				continue;
			RootFile rootFileElem = (RootFile) anyOrRoot;
			if (!rootFileElem.getFullPath().equals(path)
					&& !rootFileElem.getMediaType().equals(mediaType))
				// Different path and media type - ignore
				continue;
			if (foundExisting) {
				// Duplicate path/media type, we'll remove it
				anyOrRootIt.remove();
				continue;
			}
			rootFileElem.setFullPath(rootFile.getPath());
			if (mediaType != null)
				rootFileElem.setMediaType(mediaType);

			foundExisting = true;
		}
		if (!foundExisting) {
			RootFile rootFileElem = containerFactory.createRootFile();
			rootFileElem.setFullPath(rootFile.getPath());
			rootFileElem.setMediaType(mediaType);
			rootFiles.getAnyOrRootFile().add(
					containerFactory
							.createContainerRootFilesRootFile(rootFileElem));
			// rootFiles.getAnyOrRootFile().add(rootFileElem);
		}
	}

	protected RootFiles getRootFiles(Container container) {
		for (Object o : container.getRootFilesOrAny()) {
			if (o instanceof JAXBElement) {
				@SuppressWarnings("rawtypes")
				JAXBElement jaxbElement = (JAXBElement) o;
				o = jaxbElement.getValue();
			}
			if (o instanceof RootFiles)
				return (RootFiles) o;
		}
		// Not found - add it
		RootFiles rootFiles = containerFactory.createContainerRootFiles();
		container.getRootFilesOrAny().add(
				containerFactory.createContainerRootFiles(rootFiles));
		return rootFiles;
	}

	@SuppressWarnings("rawtypes")
	public List<ResourceEntry> getRootFiles() {
		ArrayList<UCFPackage.ResourceEntry> rootFiles = new ArrayList<>();
		if (containerXml == null)
			return rootFiles;

		RootFiles rootFilesElem = getRootFiles(containerXml.getValue());
		for (Object anyOrRoot : rootFilesElem.getAnyOrRootFile()) {
			if (anyOrRoot instanceof JAXBElement)
				anyOrRoot = ((JAXBElement) anyOrRoot).getValue();
			if (!(anyOrRoot instanceof RootFile))
				continue;
			RootFile rf = (RootFile) anyOrRoot;
			ResourceEntry entry = getResourceEntry(rf.getFullPath());
			if (rf.getMediaType() != null
					&& rf.getMediaType() != entry.mediaType)
				// Override the mime type in the returned entry
				entry.mediaType = rf.getMediaType();
			rootFiles.add(entry);
		}
		return rootFiles;
	}

	public ResourceEntry getResourceEntry(String path) {
		OdfFileEntry odfFileEntry = odfPackage.getManifestEntries().get(path);
		if (odfFileEntry == null)
			return null;
		return new ResourceEntry(odfFileEntry);
	}

	@SuppressWarnings("rawtypes")
	public void unsetRootFile(String path) {
		Container container = containerXml.getValue();
		RootFiles rootFiles = getRootFiles(container);
		Iterator<Object> anyOrRootIt = rootFiles.getAnyOrRootFile().iterator();
		while (anyOrRootIt.hasNext()) {
			Object anyOrRoot = anyOrRootIt.next();
			if (anyOrRoot instanceof JAXBElement)
				anyOrRoot = ((JAXBElement) anyOrRoot).getValue();
			if (!(anyOrRoot instanceof RootFile))
				continue;
			RootFile rootFileElem = (RootFile) anyOrRoot;
			if (rootFileElem.getFullPath().equals(path))
				anyOrRootIt.remove();
		}
	}

	protected JAXBElement<Container> getContainerXML() {
		return containerXml;
	}

	public void save(OutputStream output) throws IOException {
		File tempFile = createTempFile("ucfpackage", ".tmp");
		prepareAndSave(tempFile);

		// Copy file to the output

		// Note - Should use IOUtils, but we're trying to avoid external dependencies
		try (InputStream inStream = new FileInputStream(tempFile)) {
			byte[] buffer = new byte[8192];
			int n = 0;
			do {
				output.write(buffer, 0, n);
				n = inStream.read(buffer);
			} while (n > -1);
		} finally {
			tempFile.delete();
		}
	}

	public OutputStream addResourceUsingOutputStream(String path,
			String mediaType) throws IOException {
		if (path.equals(CONTAINER_XML))
			// as we need to parse it after insertion, this must fail
			throw new IllegalArgumentException("Can't add " + CONTAINER_XML
					+ " using OutputStream");
		try {
			return odfPackage.insertOutputStream(path, mediaType);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not add " + path, e);
		}
	}

	@Override
	public UCFPackage clone() {
		final PipedOutputStream outputStream = new PipedOutputStream();
		try {
			try (PipedInputStream inputStream = copyToOutputStream(outputStream)) {
				return new UCFPackage(inputStream);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not clone UCFPackage", e);
		}
	}

	private PipedInputStream copyToOutputStream(
			final PipedOutputStream outputStream) throws IOException {
		PipedInputStream inputStream = new PipedInputStream(outputStream);
		new Thread("Cloning " + this) {
			@Override
			public void run() {
				try {
					try {
						save(outputStream);
					} finally {
						outputStream.close();
					}
				} catch (IOException e) {
					logger.log(INFO,
							"Could not save/close UCF package while cloning", e);
				}
			}
		}.start();
		return inputStream;
	}

	public String getRootFileVersion(String rootFile) {
		return getResourceEntry(rootFile).getVersion();
	}
}
