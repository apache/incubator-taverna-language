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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.apache.taverna.scufl2.ucfpackage.impl.odfdom.pkg.OdfPackage;
//import org.apache.taverna.scufl2.ucfpackage.impl.odfdom.pkg.manifest.OdfFileEntry;
import org.oasis_open.names.tc.opendocument.xmlns.container.Container;
import org.oasis_open.names.tc.opendocument.xmlns.container.Container.RootFiles;
import org.oasis_open.names.tc.opendocument.xmlns.container.ObjectFactory;
import org.oasis_open.names.tc.opendocument.xmlns.container.RootFile;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;
import org.purl.wf4ever.robundle.manifest.Manifest;
import org.purl.wf4ever.robundle.manifest.PathMetadata;
import org.purl.wf4ever.robundle.utils.RecursiveDeleteVisitor;
import org.purl.wf4ever.robundle.utils.TemporaryFiles;
import org.w3c.dom.Document;

public class UCFPackage implements Cloneable {
	private static Logger logger = Logger.getLogger(UCFPackage.class.getName());
	private static final URI VERSION_BASE = URI.create("http://ns.taverna.org.uk/2010/scufl2/");
	private static final String CONTAINER_XML = "META-INF/container.xml";
	private static final Charset UTF_8 = Charset.forName("utf-8");
	public static final String MIME_BINARY = "application/octet-stream";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String MIME_TEXT_XML = "text/xml";
	public static final String MIME_RDF = "application/rdf+xml";
	public static final String MIME_EPUB = "application/epub+zip";
	public static final String MIME_WORKFLOW_BUNDLE = "application/vnd.taverna.workflow-bundle";

	private static Charset ASCII = Charset.forName("ascii");
//	private OdfPackage odfPackage;
	private static JAXBContext jaxbContext;
	private JAXBElement<Container> containerXml;
	private boolean createdContainerXml = false;
    private Bundle bundle;
	private static ObjectFactory containerFactory = new ObjectFactory();

	public UCFPackage() throws IOException {
		try {
		    bundle = Bundles.createBundle();
			//odfPackage = OdfPackage.create();
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
	    bundle = Bundles.openBundleReadOnly(containerFile.toPath());
	    parseContainerXML();
	}

	public UCFPackage(InputStream inputStream) throws IOException {
		open(inputStream);
	}

	protected UCFPackage(Bundle bundle) throws IOException {
	    this.bundle = bundle;
	    parseContainerXML();
    }

    protected void open(InputStream inputStream) throws IOException {
		try {
		    Path bundlePath = TemporaryFiles.temporaryBundle();
		    Files.copy(inputStream, bundlePath);
		    bundle = Bundles.openBundle(bundlePath);
		    bundle.setDeleteOnClose(true);

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
	    try {
            return Bundles.getMimeType(bundle);
        } catch (IOException e) {
            return MIME_WORKFLOW_BUNDLE;
        }
	}

	public void setPackageMediaType(String mediaType) {
		try {
					Bundles.setMimeType(bundle, mediaType);
			} catch (IOException e) {
					throw new RuntimeException("Can't set media type", e);
			}
}

	public void save(File packageFile) throws IOException {
	    prepareContainerXML();

	    Path source = bundle.getSource();
	    boolean deleteOnClose = bundle.isDeleteOnClose();
	    bundle.setDeleteOnClose(false);
	    Bundles.closeAndSaveBundle(bundle, packageFile.toPath());
	    // Re-open the original source (usually a tmpfile)
	    bundle = Bundles.openBundle(source);
	    bundle.setDeleteOnClose(deleteOnClose);
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
			Path containerPath = writableBundlePath(CONTAINER_XML);
			outStream = Files.newOutputStream(containerPath);

			// FIXME: Set namespace prefixes and default namespace

			marshaller.setProperty("jaxb.formatted.output", true);

			// TODO: Ensure using default namespace
			marshaller.marshal(containerXml, outStream);

		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not parse " + CONTAINER_XML, e);
		}
	}

	protected static synchronized Marshaller createMarshaller()
			throws JAXBException {
		return getJaxbContext().createMarshaller();
	}

	protected static synchronized Unmarshaller createUnMarshaller()
			throws JAXBException {
		return getJaxbContext().createUnmarshaller();
	}

	protected static synchronized JAXBContext getJaxbContext()
			throws JAXBException {
		if (jaxbContext == null)
			jaxbContext = JAXBContext
					.newInstance(
							org.oasis_open.names.tc.opendocument.xmlns.container.ObjectFactory.class,
							org.w3._2000._09.xmldsig_.ObjectFactory.class,
							org.w3._2001._04.xmlenc_.ObjectFactory.class);
		return jaxbContext;
	}

	public void addResource(String stringValue, String path, String mediaType)
			throws IOException {
	    Path bundlePath = writableBundlePath(path);
	    Bundles.setStringValue(bundlePath, stringValue);
	    Manifest manifest = bundle.getManifest();
	    manifest.getAggregation(bundlePath).setMediatype(mediaType);
	}

	public void addResource(byte[] bytesValue, String path, String mediaType)
			throws IOException {

	    Path bundlePath = writableBundlePath(path);
	    Files.write(bundlePath, bytesValue);
        Manifest manifest = bundle.getManifest();
        manifest.getAggregation(bundlePath).setMediatype(mediaType);
	}

    private Path writableBundlePath(String path) {
        Path bundlePath = bundle.getRoot().resolve(path);
        if (bundlePath.getParent() != null) {
            try {
                Files.createDirectories(bundlePath.getParent());
            } catch (IOException e) {
                throw new RuntimeException("Could not create parent directories of " + path, e);
            }
        }
        return bundlePath;
    }

	@Deprecated
	public void addResource(Document document, String path, String mediaType)
			throws IOException {

        Path bundlePath = writableBundlePath(path);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IOException("Can't create XML transformer to save "
                    + path, e);
        }

        DOMSource source = new DOMSource(document);
        try (OutputStream outStream = Files.newOutputStream(bundlePath)) {
            StreamResult result = new StreamResult(outStream);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException("Can't save XML to " + path, e);
        }

		if (path.equals(CONTAINER_XML)) {
			parseContainerXML();
	}

	public void addResource(InputStream inputStream, String path,
			String mediaType) throws IOException {
	    Path bundlePath = writableBundlePath(path);
	    Files.copy(inputStream, bundlePath);
        Manifest manifest = bundle.getManifest();
        manifest.getAggregation(bundlePath).setMediatype(mediaType);
	}

	public void addResource(URI uri, String path, String mediaType)
			throws IOException {
	    Path bundlePath = writableBundlePath(path);
        Bundles.setReference(bundlePath, uri);
        Manifest manifest = bundle.getManifest();
        manifest.getAggregation(bundlePath).setMediatype(mediaType);
	}

	public String getResourceAsString(String path) throws IOException {
	    Path bundlePath = bundle.getRoot().resolve(path);
	    return Bundles.getStringValue(bundlePath);
	}

	public byte[] getResourceAsBytes(String path) throws IOException {
	    Path bundlePath = bundle.getRoot().resolve(path);
        return Files.readAllBytes(bundlePath);
	}

	public InputStream getResourceAsInputStream(String path) throws IOException {
	    Path bundlePath = bundle.getRoot().resolve(path);
	    if (! Files.isReadable(bundlePath)) {
	        return null;
	    }
	    return Files.newInputStream(bundlePath);
	}

	public Map<String, ResourceEntry> listResources() {
		return listResources("", false);
	}

	public Map<String, ResourceEntry> listResources(String folderPath) {
		return listResources(folderPath, false);
	}

	protected Map<String, ResourceEntry> listResources(String folderPath,
			boolean recursive) {
	    Path bundlePath = bundle.getRoot().resolve(folderPath);
	    List<Path> reserved = Arrays.asList(bundle.getRoot().resolve("META-INF/"),
	            bundle.getRoot().resolve(".ro/"),
	            bundle.getRoot().resolve("mimetype")
	            );

	    HashMap<String, ResourceEntry> content = new HashMap<String, ResourceEntry>();
	    try (DirectoryStream<Path> ds = Files.newDirectoryStream(bundlePath)) {
	        for (Path path : ds) {
	            if (reserved.contains(path)) {
	                continue;
	            }
	            content.put(path.toString(), new ResourceEntry(path));
	        }
	    } catch (IOException e) {
            throw new RuntimeException("Can't list resources of "  +folderPath, e);
        }
		return content;
	}

	public void removeResource(String path) {
	    Path bundlePath = bundle.getRoot().resolve(path);
	    try {
            RecursiveDeleteVisitor.deleteRecursively(bundlePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete " + path + " or its children", e);
        }
	}

	public class ResourceEntry {

        private Path path;

		public ResourceEntry(Path path) {
            this.path = path;
        }

        public String getPath() {
			return path.toString().replaceFirst("^/", "");
		}

		public long getSize() {
			try {
                return Files.size(path);
            } catch (IOException e) {
               throw new RuntimeException("Can't determine size of " + path, e);
            }
		}

		public String getMediaType() {
			try {
                return bundle.getManifest().getAggregation(path).getMediatype();
            } catch (IOException e) {
                throw new RuntimeException("Can't get media type for " + path, e);
            }
		}

		public boolean isFolder() {
			return Files.isDirectory(path);
		}

		public UCFPackage getUcfPackage() {
			return UCFPackage.this;
		}

		@Override
		public String toString() {
		    return getPath();
		};

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ResourceEntry))
				return false;
			ResourceEntry other = (ResourceEntry) obj;

			if (!getUcfPackage().equals(other.getUcfPackage()))
				return false;
			}
			return path.equals(other.path);
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
            URI conformsTo;
            try {
                conformsTo = bundle.getManifest().getAggregation(path).getConformsTo();
            } catch (IOException e) {
                throw new RuntimeException("Can't look up version for " + path, e);
            }
            if (conformsTo != null) {
                URI version = VERSION_BASE.relativize(conformsTo);
                if (!version.isAbsolute()) {
                    return version.toString();
                }
            }
            return null;

        }
	}

	public Map<String, ResourceEntry> listAllResources() {
		return listResources("/", true);
	}

	public void setRootFile(String path) {
		setRootFile(path, null);
	}

	@SuppressWarnings("rawtypes")
	public void setRootFile(String path, String version) {
		ResourceEntry rootFile = getResourceEntry(path);
		if (rootFile == null)
			throw new IllegalArgumentException("Unknown resource: " + path);
		}

		URI conformsTo = null;
		if (version != null) {
		    conformsTo = VERSION_BASE.resolve(version);
		    PathMetadata aggregation;
            try {
                aggregation = bundle.getManifest().getAggregation(rootFile.path);
            } catch (IOException e) {
                throw new RuntimeException("Can't get root file aggregation for " + path, e);
            }
		    aggregation.setConformsTo(conformsTo);
		}

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
					&& rf.getMediaType() != entry.getMediaType()) {
				// Override the mime type in the returned entry
			    PathMetadata aggr;
                try {
                    aggr = bundle.getManifest().getAggregation(entry.path);
                } catch (IOException e) {
                    throw new RuntimeException("Can't get aggregation for " + entry, e);
                }
			    aggr.setMediatype(rf.getMediaType());
			}
			rootFiles.add(entry);
		}
		return rootFiles;
	}

	public ResourceEntry getResourceEntry(String path) {
	    Path bundlePath = bundle.getRoot().resolve(path);
	    if (Files.exists(bundlePath)) {
	    	return new ResourceEntry(bundlePath);
	    }
	    return null;
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
        prepareContainerXML();
        Path source = bundle.getSource();
        boolean deleteOnClose = bundle.isDeleteOnClose();
        bundle.setDeleteOnClose(false);
        Bundles.closeBundle(bundle);

        Files.copy(source, output);

        // Re-open the original source (usually a tmpfile)
        bundle = Bundles.openBundle(source);
        bundle.setDeleteOnClose(deleteOnClose);
	}

	public OutputStream addResourceUsingOutputStream(String path,
			String mediaType) throws IOException {
		if (path.equals(CONTAINER_XML))
			// as we need to parse it after insertion, this must fail
			throw new IllegalArgumentException("Can't add " + CONTAINER_XML
					+ " using OutputStream");
			// as we need to parse it after insertion
		}
		Path bundlePath = writableBundlePath(path);
	    return Files.newOutputStream(bundlePath);
	}

	@Override
	public UCFPackage clone() {

	        try {
                prepareContainerXML();
            } catch (IOException e) {
                throw new RuntimeException("Could not prepare " + CONTAINER_XML, e);
            }

	        Path source = bundle.getSource();
	        boolean deleteOnClose = bundle.isDeleteOnClose();
	        bundle.setDeleteOnClose(false);
	        try {
                Bundles.closeBundle(bundle);
            } catch (IOException e) {
                throw new RuntimeException("Could not save bundle to " + source, e);
            }

	        Bundle clonedBundle;
            try {
                clonedBundle = Bundles.openBundleReadOnly(source);
            } catch (IOException e) {
                throw new RuntimeException("Could not copy bundle from " + source, e);
            }

	        // Re-open the original source (usually a tmpfile)
	        try {
                bundle = Bundles.openBundle(source);
                bundle.setDeleteOnClose(deleteOnClose);
                return new UCFPackage(clonedBundle);
            } catch (IOException e) {
                throw new RuntimeException("Could not re-open from " + source, e);
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
