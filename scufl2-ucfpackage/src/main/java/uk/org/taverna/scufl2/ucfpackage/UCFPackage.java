package uk.org.taverna.scufl2.ucfpackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.oasis_open.names.tc.opendocument.xmlns.container.Container;
import org.oasis_open.names.tc.opendocument.xmlns.container.Container.RootFiles;
import org.oasis_open.names.tc.opendocument.xmlns.container.ObjectFactory;
import org.oasis_open.names.tc.opendocument.xmlns.container.RootFile;
import org.w3c.dom.Document;

import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;
import uk.org.taverna.scufl2.ucfpackage.impl.odfdom.pkg.OdfPackage;
import uk.org.taverna.scufl2.ucfpackage.impl.odfdom.pkg.manifest.OdfFileEntry;

public class UCFPackage {

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
	private final List<String> rootFilePaths = new ArrayList<String>();
	private static JAXBContext jaxbContext;

	public UCFPackage() throws Exception {
		odfPackage = OdfPackage.create();
		// odfPackage.setMediaType(MIME_EPUB);
	}

	public UCFPackage(File containerFile) throws Exception {
		odfPackage = OdfPackage.loadPackage(containerFile);
	}

	public UCFPackage(InputStream inputStream) throws Exception {
		odfPackage = OdfPackage.loadPackage(inputStream);
	}

	public String getPackageMediaType() {
		return odfPackage.getMediaType();
	}

	public void setPackageMediaType(String mediaType) {
		if (mediaType == null || !mediaType.contains("/")) {
			throw new IllegalArgumentException("Invalid media type "
					+ mediaType);
		}
		if (!ASCII.newEncoder().canEncode(mediaType)) {
			throw new IllegalArgumentException("Media type must be ASCII: "
					+ mediaType);
		}
		odfPackage.setMediaType(mediaType);
	}

	public void save(File packageFile) throws IOException {
		if (getPackageMediaType() == null) {
			throw new IllegalStateException("Package media type must be set");
		}

		// Write using temp file, and do rename in the end
		File tempFile = File.createTempFile("." + packageFile.getName(),
				".tmp", packageFile.getParentFile());
		try {
			prepareContainerXML();
			odfPackage.save(tempFile);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not save bundle to " + packageFile, e);
		} finally {
			odfPackage.close();
		}
		try {
			// To be safe we'll reload from 'our' tempFile
			odfPackage = OdfPackage.loadPackage(tempFile);
		} catch (Exception e) {
			throw new IOException("Could not reload package from "
					+ packageFile);
		}
		if (!tempFile.renameTo(packageFile)) {
			throw new IOException("Could not rename temp file " + tempFile
					+ " to " + packageFile);
		}
	}

	protected void prepareContainerXML() throws Exception {
		if (!rootFilePaths.isEmpty()) {

			ObjectFactory containerFactory = new ObjectFactory();
			Container container = containerFactory.createContainer();
			RootFiles rootFiles = containerFactory.createContainerRootFiles();
			container.setRootFiles(rootFiles);

			for (ResourceEntry rootFile : getRootFiles()) {
				RootFile rootFileElem = containerFactory.createRootFile();
				rootFileElem.setFullPath(rootFile.getPath());
				rootFileElem.setMediaType(rootFile.getMediaType());
				rootFiles.getRootFile().add(rootFileElem);
			}
			Marshaller marshaller = createMarshaller();

			OutputStream outStream = odfPackage
					.insertOutputStream("META-INF/container.xml");
			try {
				JAXBElement<Container> containerElem = containerFactory
						.createContainer(container);

				// XMLStreamWriter xmlStreamWriter = XMLOutputFactory
				// .newInstance().createXMLStreamWriter(outStream);
				// xmlStreamWriter.setDefaultNamespace(containerElem.getName()
				// .getNamespaceURI());
				//
				// xmlStreamWriter.setPrefix("dsig",
				// "http://www.w3.org/2000/09/xmldsig#");
				// xmlStreamWriter.setPrefix("xmlenc",
				// "http://www.w3.org/2001/04/xmlenc#");

				// FIXME: Set namespace prefixes and default namespace

				marshaller.setProperty("jaxb.formatted.output", true);


				// TODO: Ensure using default namespace
				marshaller.marshal(containerElem, outStream);

			} finally {
				outStream.close();
			}

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
		if (jaxbContext == null) {
			jaxbContext = JAXBContext.newInstance(
					"org.oasis_open.names.tc.opendocument.xmlns.container:"
							+ "org.w3._2000._09.xmldsig_:"
							+ "org.w3._2001._04.xmlenc_",
					UCFPackage.class.getClassLoader());
		}
		return jaxbContext;
	}

	public void addResource(String stringValue, String path, String mediaType)
			throws Exception {
		odfPackage.insert(stringValue.getBytes(UTF_8), path, mediaType);
	}

	public void addResource(byte[] bytesValue, String path, String mediaType)
			throws Exception {
		odfPackage.insert(bytesValue, path, mediaType);
	}

	public void addResource(Document document, String path, String mediaType)
			throws Exception {
		odfPackage.insert(document, path, mediaType);
	}

	public void addResource(InputStream inputStream, String path,
			String mediaType) throws Exception {
		odfPackage.insert(inputStream, path, mediaType);
	}

	public void addResource(URI uri, String path, String mediaType)
			throws Exception {
		odfPackage.insert(uri, path, mediaType);
	}

	public String getResourceAsString(String path) throws Exception {
		return new String(odfPackage.getBytes(path), UTF_8);
	}

	public byte[] getResourceAsBytes(String path) throws Exception {
		return odfPackage.getBytes(path);
	}

	public InputStream getResourceAsInputStream(String path) throws Exception {
		return odfPackage.getInputStream(path);
	}

	public Map<String, ResourceEntry> listResources() {
		return listResources("", false);
	}

	public Map<String, ResourceEntry> listResources(String folderPath) {
		return listResources(folderPath, false);
	}

	protected Map<String, ResourceEntry> listResources(String folderPath,
			boolean recursive) {
		if (!folderPath.isEmpty() && !folderPath.endsWith("/")) {
			folderPath = folderPath + "/";
		}
		HashMap<String, ResourceEntry> content = new HashMap<String, ResourceEntry>();

		for (Entry<String, OdfFileEntry> entry : odfPackage
				.getManifestEntries().entrySet()) {
			String entryPath = entry.getKey();
			if (!entryPath.startsWith(folderPath)) {
				continue;
			}
			String subPath = entryPath.substring(folderPath.length(),
					entryPath.length());
			if (subPath.isEmpty()) {
				// The folder itself
				continue;
			}
			int firstSlash = subPath.indexOf("/");
			if (!recursive && firstSlash > -1
					&& firstSlash < subPath.length() - 1) {
				// Children of a folder (note that we'll include the folder
				// itself which ends in /)
				continue;
			}
			content.put(subPath, new ResourceEntry(entry.getValue()));
		}
		return content;
	}

	public void removeResource(String path) {
		if (!odfPackage.contains(path)) {
			return;
		}
		if (path.endsWith("/")) {
			for (ResourceEntry childEntry : listResources(path).values()) {
				removeResource(childEntry.getPath());
			}
		}
		odfPackage.remove(path);
	}

	public class ResourceEntry {

		private final String path;
		private final long size;
		private final String mediaType;

		protected ResourceEntry(OdfFileEntry odfEntry) {
			path = odfEntry.getPath();
			size = odfEntry.getSize();
			mediaType = odfEntry.getMediaType();
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

	}

	public Map<String, ResourceEntry> listAllResources() {
		return listResources("", true);
	}

	public void setRootFile(String path) {
		if (getResourceEntry(path) == null) {
			throw new IllegalArgumentException("Unknown resource: " + path);
		}
		rootFilePaths.add(path);
	}

	public List<ResourceEntry> getRootFiles() {
		ArrayList<UCFPackage.ResourceEntry> rootFiles = new ArrayList<UCFPackage.ResourceEntry>();
		for (String rootPath : rootFilePaths) {
			rootFiles.add(getResourceEntry(rootPath));
		}
		return rootFiles;
	}

	public ResourceEntry getResourceEntry(String path) {
		OdfFileEntry odfFileEntry = odfPackage.getManifestEntries().get(path);
		if (odfFileEntry == null) {
			return null;
		}
		return new ResourceEntry(odfFileEntry);
	}

	public void unsetRootFile(String path) {
		rootFilePaths.remove(path);
	}

}
